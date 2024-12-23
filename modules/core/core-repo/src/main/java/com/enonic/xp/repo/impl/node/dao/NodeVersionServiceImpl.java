package com.enonic.xp.repo.impl.node.dao;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.core.internal.MemoryLimitParser;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.ImmutableNodeVersion;
import com.enonic.xp.repo.impl.node.json.ImmutableProperty;
import com.enonic.xp.repo.impl.node.json.ImmutableVersionData;
import com.enonic.xp.repo.impl.node.json.NodeVersionAccessControl;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;

@Component
public class NodeVersionServiceImpl
    implements NodeVersionService
{
    private final BlobStore blobStore;

    private final Cache<BlobKey, WithWeight<ImmutableNodeVersion>> nodeDataCache;

    private final Cache<BlobKey, WithWeight<PatternIndexConfigDocument>> indexConfigCache;

    private final Cache<BlobKey, WithWeight<NodeVersionAccessControl>> accessControlCache;

    @Activate
    public NodeVersionServiceImpl( @Reference final BlobStore blobStore, @Reference final RepoConfiguration repoConfiguration )
    {
        this.blobStore = blobStore;
        final long cacheCapacity = MemoryLimitParser.maxHeap().parse( repoConfiguration.cacheCapacity() );

        final long nodeCacheCapacity = (long) ( cacheCapacity * 0.98D );
        final long otherCachesCapacity = (long) ( cacheCapacity * 0.01D );

        this.nodeDataCache = CacheBuilder.newBuilder().maximumWeight( nodeCacheCapacity ).weigher( WithWeight.WEIGHTER ).build();
        this.indexConfigCache = CacheBuilder.newBuilder().maximumWeight( otherCachesCapacity ).weigher( WithWeight.WEIGHTER ).build();
        this.accessControlCache = CacheBuilder.newBuilder().maximumWeight( otherCachesCapacity ).weigher( WithWeight.WEIGHTER ).build();
    }

    @Override
    public NodeVersionKey store( final NodeVersion nodeVersion, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();

        final BlobKey accessControlBlobKey =
            serializeAndAddBlobRecord( nodeVersion, repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL,
                                       NodeVersionJsonSerializer::toAccessControlBytes );
        final BlobKey indexConfigBlobKey = serializeAndAddBlobRecord( nodeVersion, repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL,
                                                                      NodeVersionJsonSerializer::toIndexConfigDocumentBytes );
        final BlobKey nodeBlobKey = serializeAndAddBlobRecord( nodeVersion, repositoryId, NodeConstants.NODE_SEGMENT_LEVEL,
                                                               NodeVersionJsonSerializer::toNodeVersionBytes );

        return NodeVersionKey.from( nodeBlobKey, indexConfigBlobKey, accessControlBlobKey );
    }

    private BlobKey serializeAndAddBlobRecord( final NodeVersion nodeVersion, final RepositoryId repositoryId, final SegmentLevel segmentLevel,
                                                   IOFunction<NodeVersion, byte[]> serializer )
    {
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( repositoryId, segmentLevel );
        final byte[] nodeJson;
        try
        {
            nodeJson = serializer.apply( nodeVersion );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        return blobStore.addRecord( nodeSegment, ByteSource.wrap( nodeJson ) ).getKey();
    }

    @Override
    public NodeVersion get( final NodeVersionKey nodeVersionKey, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();

        final NodeVersionAccessControl accessControl =
            fetchAndDeserializeCached( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL, nodeVersionKey.getAccessControlBlobKey(),
                                       NodeVersionJsonSerializer::toNodeVersionAccessControl, accessControlCache );

        final PatternIndexConfigDocument indexConfigDocument =
            fetchAndDeserializeCached( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL, nodeVersionKey.getIndexConfigBlobKey(),
                                       NodeVersionJsonSerializer::toIndexConfigDocument, indexConfigCache );

        final ImmutableNodeVersion immutableNodeVersion =
            fetchAndDeserializeCached( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL, nodeVersionKey.getNodeBlobKey(),
                                       ImmutableVersionData::deserialize, nodeDataCache );

        return NodeVersion.create()
            .id( immutableNodeVersion.id )
            .nodeType( immutableNodeVersion.nodeType )
            .data( toPropertyTree( immutableNodeVersion.data ) )
            .indexConfigDocument( indexConfigDocument )
            .childOrder( immutableNodeVersion.childOrder )
            .manualOrderValue( immutableNodeVersion.manualOrderValue )
            .attachedBinaries( immutableNodeVersion.attachedBinaries )
            .permissions( accessControl.getPermissions() )
            .inheritPermissions( accessControl.isInheritPermissions() )
            .build();
    }

    private static PropertyTree toPropertyTree( final List<ImmutableProperty> data )
    {
        final PropertyTree result = new PropertyTree();
        ImmutableProperty.addToSet( result.getRoot(), data );
        return result;
    }

    private <T> T fetchAndDeserializeCached( final RepositoryId repositoryId, final SegmentLevel segmentLevel, final BlobKey blobKey,
                                             final IOFunction<ByteSource, T> deserializer, Cache<BlobKey, WithWeight<T>> cache )
    {
        try
        {
            return cache.get( blobKey, () -> fetchAndDeserialize( repositoryId, segmentLevel, blobKey, deserializer ) ).value;
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException( String.format( "Failed to load blob %s [%s/%s]", blobKey, repositoryId, segmentLevel ),
                                        e.getCause() );
        }
    }

    private <T> WithWeight<T> fetchAndDeserialize( RepositoryId repositoryId, SegmentLevel segmentLevel, BlobKey blobKey,
                                                   final IOFunction<ByteSource, T> deserializer )
        throws IOException
    {
        final Segment segment = RepositorySegmentUtils.toSegment( repositoryId, segmentLevel );
        final BlobRecord blobRecord = blobStore.getRecord( segment, blobKey );
        if ( blobRecord == null )
        {
            throw new IllegalStateException( String.format( "Blob record not found %s [%s/%s]", blobKey, repositoryId, segmentLevel ) );
        }
        final ByteSource bytes = blobRecord.getBytes();
        return new WithWeight<>( deserializer.apply( bytes ), blobRecord.getLength() );
    }

    private static class WithWeight<T>
    {
        final T value;

        final int weight;

        WithWeight( final T value, final long weight )
        {
            this.value = value;
            this.weight = (int) Math.min( weight, Integer.MAX_VALUE );
        }

        static final Weigher<BlobKey, WithWeight<?>> WEIGHTER = ( key, value ) -> value.weight;
    }

    @FunctionalInterface
    private interface IOFunction<T, R> {
        R apply( T t) throws IOException;
    }
}
