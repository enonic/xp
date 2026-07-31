package com.enonic.xp.repo.impl.node.dao;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.ImmutableNodeVersion;
import com.enonic.xp.repo.impl.node.json.ImmutableProperty;
import com.enonic.xp.repo.impl.node.json.ImmutableVersionData;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.security.acl.AccessControlList;

@Component
public class NodeVersionServiceImpl
    implements NodeVersionService
{
    private final BlobStore blobStore;

    private final BoundedCache<ImmutableNodeVersion> nodeDataCache;

    private final BoundedCache<PatternIndexConfigDocument> indexConfigCache;

    private final BoundedCache<AccessControlList> accessControlCache;

    @Activate
    public NodeVersionServiceImpl( @Reference final BlobStore blobStore, @Reference final RepoConfiguration repoConfiguration )
    {
        this.blobStore = blobStore;
        final long cacheCapacity = repoConfiguration.cacheCapacity();

        final long nodeCacheCapacity = (long) ( cacheCapacity * 0.98D );
        final long otherCachesCapacity = (long) ( cacheCapacity * 0.01D );

        this.nodeDataCache = new BoundedCache<>( nodeCacheCapacity );
        this.indexConfigCache = new BoundedCache<>( otherCachesCapacity );
        this.accessControlCache = new BoundedCache<>( otherCachesCapacity );
    }

    @Override
    public NodeVersionKey store( final NodeStoreVersion nodeVersion, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();

        final BlobKey accessControlBlobKey =
            serializeAndAddBlobRecord( nodeVersion, repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL,
                                       NodeVersionJsonSerializer::toAccessControlBytes );
        final BlobKey indexConfigBlobKey = serializeAndAddBlobRecord( nodeVersion, repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL,
                                                                      NodeVersionJsonSerializer::toIndexConfigDocumentBytes );
        final BlobKey nodeBlobKey = serializeAndAddBlobRecord( nodeVersion, repositoryId, NodeConstants.NODE_SEGMENT_LEVEL,
                                                               NodeVersionJsonSerializer::toNodeVersionBytes );

        return NodeVersionKey.create()
            .nodeBlobKey( nodeBlobKey )
            .indexConfigBlobKey( indexConfigBlobKey )
            .accessControlBlobKey( accessControlBlobKey )
            .build();
    }

    private BlobKey serializeAndAddBlobRecord( final NodeStoreVersion nodeVersion, final RepositoryId repositoryId,
                                               final SegmentLevel segmentLevel, IOFunction<NodeStoreVersion, byte[]> serializer )
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
    public NodeStoreVersion get( final NodeVersionKey nodeVersionKey, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();

        final AccessControlList accessControl =
            fetchAndDeserializeCached( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL, nodeVersionKey.getAccessControlBlobKey(),
                                       NodeVersionJsonSerializer::toNodeVersionAccessControl, accessControlCache );

        final PatternIndexConfigDocument indexConfigDocument =
            fetchAndDeserializeCached( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL, nodeVersionKey.getIndexConfigBlobKey(),
                                       NodeVersionJsonSerializer::toIndexConfigDocument, indexConfigCache );

        final ImmutableNodeVersion immutableNodeVersion =
            fetchAndDeserializeCached( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL, nodeVersionKey.getNodeBlobKey(),
                                       ImmutableVersionData::deserialize, nodeDataCache );

        return NodeStoreVersion.create()
            .id( immutableNodeVersion.id )
            .nodeType( immutableNodeVersion.nodeType )
            .data( toPropertyTree( immutableNodeVersion.data ) )
            .childOrder( immutableNodeVersion.childOrder )
            .manualOrderValue( immutableNodeVersion.manualOrderValue )
            .attachedBinaries( immutableNodeVersion.attachedBinaries )
            .indexConfigDocument( indexConfigDocument )
            .permissions( accessControl )
            .build();
    }

    @Override
    public AccessControlList getPermissions( final NodeVersionKey nodeVersionKey, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();
        return fetchAndDeserializeCached( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL,
                                          nodeVersionKey.getAccessControlBlobKey(), NodeVersionJsonSerializer::toNodeVersionAccessControl,
                                          accessControlCache );
    }

    private static PropertyTree toPropertyTree( final List<ImmutableProperty> data )
    {
        final PropertyTree result = new PropertyTree();
        ImmutableProperty.addToSet( result.getRoot(), data );
        return result;
    }

    private <T> T fetchAndDeserializeCached( final RepositoryId repositoryId, final SegmentLevel segmentLevel, final BlobKey blobKey,
                                             final IOFunction<ByteSource, T> deserializer, final BoundedCache<T> cache )
    {
        try
        {
            final AtomicReference<T> uncacheable = new AtomicReference<>();
            final WithWeight<T> cached = cache.cache.get( blobKey, key -> {
                final WithWeight<T> loaded = fetchAndDeserialize( repositoryId, segmentLevel, key, deserializer, cache.maxItemWeight );
                if ( loaded.weight >= cache.maxItemWeight )
                {
                    uncacheable.set( loaded.value );
                    return null;
                }
                return loaded;
            } );
            return cached != null ? cached.value : uncacheable.get();
        }
        catch ( UncheckedIOException e )
        {
            throw new RuntimeException( String.format( "Failed to load blob %s [%s/%s]", blobKey, repositoryId, segmentLevel ),
                                        e.getCause() );
        }
    }

    private <T> WithWeight<T> fetchAndDeserialize( final RepositoryId repositoryId, final SegmentLevel segmentLevel, final BlobKey blobKey,
                                                   final IOFunction<ByteSource, T> deserializer, final long bailOutWeight )
    {
        final Segment segment = RepositorySegmentUtils.toSegment( repositoryId, segmentLevel );
        final BlobRecord blobRecord = blobStore.getRecord( segment, blobKey );
        if ( blobRecord == null )
        {
            throw new IllegalStateException( String.format( "Blob record not found %s [%s/%s]", blobKey, repositoryId, segmentLevel ) );
        }
        try
        {
            final byte[] json = blobRecord.getBytes().read();
            final T value = deserializer.apply( ByteSource.wrap( json ) );
            return new WithWeight<>( value, WithWeight.estimateWeight( json, bailOutWeight ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static final class BoundedCache<T>
    {
        final Cache<BlobKey, WithWeight<T>> cache;

        // an entry heavier than a fraction of the whole cache would evict a large
        // number of useful entries, so such entries are not cached at all
        final long maxItemWeight;

        BoundedCache( final long capacity )
        {
            this.cache =
                Caffeine.newBuilder().maximumWeight( capacity ).<BlobKey, WithWeight<T>>weigher( ( key, value ) -> value.weight ).build();
            this.maxItemWeight = Math.max( 1, capacity / 100 );
        }
    }

    private static final class WithWeight<T>
    {
        // Memory model of a deserialized JSON blob: an object turns into headers and
        // field pointers, an array into a list with a backing array, a string into a
        // java.lang.String with its own backing array (two quotes per string), while
        // the character data is retained roughly byte-for-byte.
        private static final int CONTAINER_WEIGHT = 64;

        private static final int QUOTE_WEIGHT = 24;

        final T value;

        final int weight;

        WithWeight( final T value, final long weight )
        {
            this.value = value;
            this.weight = (int) Math.min( weight, Integer.MAX_VALUE );
        }

        static long estimateWeight( final byte[] json, final long bailOutWeight )
        {
            long weight = CONTAINER_WEIGHT + json.length;
            for ( int i = 0; i < json.length && weight < bailOutWeight; i++ )
            {
                final byte b = json[i];
                if ( b == '{' || b == '[' )
                {
                    weight += CONTAINER_WEIGHT;
                }
                else if ( b == '"' )
                {
                    weight += QUOTE_WEIGHT;
                }
            }
            return weight;
        }
    }

    @FunctionalInterface
    private interface IOFunction<T, R>
    {
        R apply( T t )
            throws IOException;
    }
}
