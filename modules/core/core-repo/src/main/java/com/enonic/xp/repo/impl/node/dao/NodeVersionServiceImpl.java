package com.enonic.xp.repo.impl.node.dao;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.ByteSource;
import com.google.common.util.concurrent.UncheckedExecutionException;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.CachingBlobStore;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
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

    private final Cache<BlobKey, ImmutableNodeVersion> nodeDataCache;

    private final Cache<BlobKey, IndexConfigDocument> indexConfigCache;

    private final Cache<BlobKey, NodeVersionAccessControl> accessControlCache;

    @Activate
    public NodeVersionServiceImpl( @Reference final BlobStore blobStore, @Reference final RepoConfiguration repoConfiguration )
    {
        this.blobStore = blobStore;
        this.nodeDataCache = CacheBuilder.newBuilder().maximumSize( repoConfiguration.cacheSize() ).build();
        this.indexConfigCache = CacheBuilder.newBuilder().maximumSize( repoConfiguration.cacheSize() ).build();
        this.accessControlCache = CacheBuilder.newBuilder().maximumSize( repoConfiguration.cacheSize() ).build();
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

                try (var is = nodeBlobRecord.getBytes().openBufferedStream())
                {
                    return ImmutableVersionData.deserialize( is );
                }
            } );

            final IndexConfigDocument indexConfigDocument = indexConfigCache.get( indexConfigBlobKey, () -> {
                final BlobRecord indexConfigBlobRecord =
                    getBlobRecord( NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL, context.getRepositoryId(), indexConfigBlobKey );
                return NodeVersionJsonSerializer.toIndexConfigDocument( indexConfigBlobRecord.getBytes() );
            } );

            final NodeVersionAccessControl accessControl = accessControlCache.get( accessControlBlobKey, () -> {
                final BlobRecord accessControlBlobRecord =
                    getBlobRecord( NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL, context.getRepositoryId(), accessControlBlobKey );
                return NodeVersionJsonSerializer.toNodeVersionAccessControl( accessControlBlobRecord.getBytes() );
            } );

            return NodeVersion.create()
                .id( immutableNodeVersion.id )
                .nodeType( immutableNodeVersion.nodeType )
                .data( toPropertyTree( immutableNodeVersion.data ) )
                .childOrder( immutableNodeVersion.childOrder )
                .manualOrderValue( immutableNodeVersion.manualOrderValue )
                .attachedBinaries( immutableNodeVersion.attachedBinaries )
                .indexConfigDocument( indexConfigDocument )
                .permissions( accessControl.getPermissions() )
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
}
