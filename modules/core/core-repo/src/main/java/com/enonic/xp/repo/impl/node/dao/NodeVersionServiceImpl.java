package com.enonic.xp.repo.impl.node.dao;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;
import com.google.common.io.ByteSource;
import com.google.common.util.concurrent.UncheckedExecutionException;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.CachingBlobStore;
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
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.NODE_SEGMENT_LEVEL );
        final byte[] nodeJsonString = NodeVersionJsonSerializer.toNodeVersionBytes( nodeVersion );
        final BlobRecord nodeBlobRecord = blobStore.addRecord( nodeSegment, ByteSource.wrap( nodeJsonString ) );

        final Segment indexConfigSegment =
            RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );
        final byte[] indexConfigDocumentString = NodeVersionJsonSerializer.toIndexConfigDocumentBytes( nodeVersion );
        final BlobRecord indexConfigBlobRecord = blobStore.addRecord( indexConfigSegment, ByteSource.wrap( indexConfigDocumentString ) );

        final Segment accessControlSegment =
            RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL );
        final byte[] accessControlString = NodeVersionJsonSerializer.toAccessControlBytes( nodeVersion );
        final BlobRecord accessControlBlobRecord = blobStore.addRecord( accessControlSegment, ByteSource.wrap( accessControlString ) );

        return NodeVersionKey.from( nodeBlobRecord.getKey(), indexConfigBlobRecord.getKey(), accessControlBlobRecord.getKey() );
    }

    @Override
    public NodeVersion get( final NodeVersionKey nodeVersionKey, final InternalContext context )
    {
        final BlobKey nodeBlobKey = nodeVersionKey.getNodeBlobKey();
        final BlobKey indexConfigBlobKey = nodeVersionKey.getIndexConfigBlobKey();
        final BlobKey accessControlBlobKey = nodeVersionKey.getAccessControlBlobKey();

        try
        {
            final ImmutableNodeVersion immutableNodeVersion = nodeDataCache.get( nodeBlobKey, () -> {
                final BlobRecord nodeBlobRecord = getBlobRecord( NodeConstants.NODE_SEGMENT_LEVEL, context.getRepositoryId(), nodeBlobKey );
                final ByteSource bytes = nodeBlobRecord.getBytes();
                try (var is = bytes.openBufferedStream())
                {
                    return new WithWeight<>( ImmutableVersionData.deserialize( is ), bytes.size() );
                }
            } ).value;

            final PatternIndexConfigDocument indexConfigDocument = indexConfigCache.get( indexConfigBlobKey, () -> {
                final BlobRecord indexConfigBlobRecord =
                    getBlobRecord( NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL, context.getRepositoryId(), indexConfigBlobKey );
                final ByteSource bytes = indexConfigBlobRecord.getBytes();
                return new WithWeight<>( NodeVersionJsonSerializer.toIndexConfigDocument( bytes ), bytes.size() );
            } ).value;

            final NodeVersionAccessControl accessControl = accessControlCache.get( accessControlBlobKey, () -> {
                final BlobRecord accessControlBlobRecord =
                    getBlobRecord( NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL, context.getRepositoryId(), accessControlBlobKey );
                final ByteSource bytes = accessControlBlobRecord.getBytes();
                return new WithWeight<>( NodeVersionJsonSerializer.toNodeVersionAccessControl( bytes ), bytes.size() );
            } ).value;

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
        catch ( ExecutionException | UncheckedExecutionException e )
        {
            if ( blobStore instanceof CachingBlobStore )
            {
                ( (CachingBlobStore) blobStore ).invalidate( null, nodeBlobKey );
                ( (CachingBlobStore) blobStore ).invalidate( null, indexConfigBlobKey );
                ( (CachingBlobStore) blobStore ).invalidate( null, accessControlBlobKey );
            }
            throw new RuntimeException(
                "Failed to load blobs with keys: " + nodeBlobKey + ", " + indexConfigBlobKey + ", " + accessControlBlobKey, e );
        }
    }

    static PropertyTree toPropertyTree( final List<ImmutableProperty> data )
    {
        final PropertyTree result = new PropertyTree();
        ImmutableProperty.addToSet( result.getRoot(), data );
        return result;
    }

    private BlobRecord getBlobRecord( SegmentLevel segmentLevel, RepositoryId repositoryId, BlobKey blobKey )
    {
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( repositoryId, segmentLevel );
        final BlobRecord nodeBlobRecord = blobStore.getRecord( nodeSegment, blobKey );
        if ( nodeBlobRecord == null )
        {
            throw new IllegalStateException( "Cannot get node blob with blobKey: " + blobKey + ". Blob is null in segment " + nodeSegment );
        }
        return nodeBlobRecord;
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
}
