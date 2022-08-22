package com.enonic.xp.repo.impl.node.dao;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.CachingBlobStore;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.blob.NodeVersionKeys;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;
import com.enonic.xp.repository.RepositorySegmentUtils;

@Component
public class NodeVersionServiceImpl
    implements NodeVersionService
{
    private final NodeVersionJsonSerializer nodeVersionJsonSerializer = NodeVersionJsonSerializer.create();

    private BlobStore blobStore;

    @Override
    public NodeVersionKey store( final NodeVersion nodeVersion, final InternalContext context )
    {
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.NODE_SEGMENT_LEVEL );
        final byte[] nodeJsonString = this.nodeVersionJsonSerializer.toNodeString( nodeVersion );
        final BlobRecord nodeBlobRecord = blobStore.addRecord( nodeSegment, ByteSource.wrap( nodeJsonString ) );

        final Segment indexConfigSegment =
            RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );
        final byte[] indexConfigDocumentString = this.nodeVersionJsonSerializer.toIndexConfigDocumentString( nodeVersion );
        final BlobRecord indexConfigBlobRecord = blobStore.addRecord( indexConfigSegment, ByteSource.wrap( indexConfigDocumentString ) );

        final Segment accessControlSegment =
            RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL );
        final byte[] accessControlString = this.nodeVersionJsonSerializer.toAccessControlString( nodeVersion );
        final BlobRecord accessControlBlobRecord = blobStore.addRecord( accessControlSegment, ByteSource.wrap( accessControlString ) );

        return NodeVersionKey.from( nodeBlobRecord.getKey(), indexConfigBlobRecord.getKey(), accessControlBlobRecord.getKey() );
    }

    @Override
    public NodeVersions get( final NodeVersionKeys nodeVersionKeys, final InternalContext context )
    {
        NodeVersions.Builder builder = NodeVersions.create();
        nodeVersionKeys.forEach( nodeVersionKey -> builder.add( getFromBlob( nodeVersionKey, context ) ) );
        return builder.build();
    }

    @Override
    public NodeVersion get( final NodeVersionKey nodeVersionKey, final InternalContext context )
    {
        return getFromBlob( nodeVersionKey, context );
    }

    private NodeVersion getFromBlob( final NodeVersionKey nodeVersionKey, final InternalContext context )
    {
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.NODE_SEGMENT_LEVEL );
        final BlobRecord nodeBlobRecord = blobStore.getRecord( nodeSegment, nodeVersionKey.getNodeBlobKey() );
        if ( nodeBlobRecord == null )
        {
            throw new IllegalArgumentException(
                "Cannot get node blob with blobKey: " + nodeVersionKey.getNodeBlobKey() + ": blob is null" );
        }

        final Segment indexConfigSegment =
            RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );
        final BlobRecord indexConfigBlobRecord = blobStore.getRecord( indexConfigSegment, nodeVersionKey.getIndexConfigBlobKey() );
        if ( indexConfigBlobRecord == null )
        {
            throw new IllegalArgumentException(
                "Cannot get index config blob with blobKey: " + nodeVersionKey.getIndexConfigBlobKey() + ": blob is null" );
        }

        final Segment accessControlSegment =
            RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL );
        final BlobRecord accessControlBlobRecord = blobStore.getRecord( accessControlSegment, nodeVersionKey.getAccessControlBlobKey() );
        if ( accessControlBlobRecord == null )
        {
            throw new IllegalArgumentException(
                "Cannot get access control blob with blobKey: " + nodeVersionKey.getAccessControlBlobKey() + ": blob is null" );
        }

        try
        {
            final byte[] nodeString = nodeBlobRecord.getBytes().read();
            final byte[] indexConfigString = indexConfigBlobRecord.getBytes().read();
            final byte[] accessControlString = accessControlBlobRecord.getBytes().read();
            return this.nodeVersionJsonSerializer.toNodeVersion( nodeString, indexConfigString, accessControlString );
        }
        catch ( IOException e )
        {
            if ( blobStore instanceof CachingBlobStore )
            {
                ( (CachingBlobStore) blobStore ).invalidate( indexConfigSegment, nodeBlobRecord.getKey() );
                ( (CachingBlobStore) blobStore ).invalidate( indexConfigSegment, indexConfigBlobRecord.getKey() );
                ( (CachingBlobStore) blobStore ).invalidate( indexConfigSegment, accessControlBlobRecord.getKey() );
            }
            throw new RuntimeException(
                "Failed to load blobs with keys: " + nodeBlobRecord.getKey() + ", " + indexConfigBlobRecord.getKey() + ", " +
                    accessControlBlobRecord.getKey(), e );
        }
    }

    @Reference
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }
}
