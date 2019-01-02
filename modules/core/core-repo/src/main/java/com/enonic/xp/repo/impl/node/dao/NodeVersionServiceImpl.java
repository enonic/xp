package com.enonic.xp.repo.impl.node.dao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Charsets;
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
    private final NodeVersionJsonSerializer nodeVersionJsonSerializer = NodeVersionJsonSerializer.create( false );

    private BlobStore blobStore;

    @Override
    public NodeVersionKey store( final NodeVersion nodeVersion, final InternalContext context )
    {
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.NODE_SEGMENT_LEVEL );
        final String nodeJsonString = this.nodeVersionJsonSerializer.toNodeString( nodeVersion );
        final ByteSource nodeByteSource = ByteSource.wrap( nodeJsonString.getBytes( StandardCharsets.UTF_8 ) );
        final BlobRecord nodeBlobRecord = blobStore.addRecord( nodeSegment, nodeByteSource );

        final Segment indexConfigSegment =
            RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );
        final String indexConfigDocumentString = this.nodeVersionJsonSerializer.toIndexConfigDocumentString( nodeVersion );
        final ByteSource indexConfigByteSource = ByteSource.wrap( indexConfigDocumentString.getBytes( StandardCharsets.UTF_8 ) );
        final BlobRecord indexConfigBlobRecord = blobStore.addRecord( indexConfigSegment, indexConfigByteSource );

        return NodeVersionKey.from( nodeBlobRecord.getKey(), indexConfigBlobRecord.getKey() );
    }

    @Override
    public NodeVersions get( final NodeVersionKeys nodeVersionKeys, final InternalContext context )
    {
        return doGetNodeVersions( nodeVersionKeys, context );
    }

    @Override
    public NodeVersion get( final NodeVersionKey nodeVersionKey, final InternalContext context )
    {
        return doGetNodeVersion( nodeVersionKey, context );
    }

    private NodeVersions doGetNodeVersions( final NodeVersionKeys nodeVersionKeys, final InternalContext context )
    {
        NodeVersions.Builder builder = NodeVersions.create();

        for ( final NodeVersionKey nodeVersionKey : nodeVersionKeys )
        {
            builder.add( doGetNodeVersion( nodeVersionKey, context ) );
        }

        return builder.build();
    }

    private NodeVersion doGetNodeVersion( final NodeVersionKey nodeVersionKey, final InternalContext context )
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

        try
        {
            final String nodeString = nodeBlobRecord.getBytes().asCharSource( Charsets.UTF_8 ).read();
            final String indexConfigString = indexConfigBlobRecord.getBytes().asCharSource( Charsets.UTF_8 ).read();
            return this.nodeVersionJsonSerializer.toNodeVersion( nodeString, indexConfigString );
        }
        catch ( IOException e )
        {
            if ( blobStore instanceof CachingBlobStore )
            {
                ( (CachingBlobStore) blobStore ).invalidate( indexConfigSegment, nodeBlobRecord.getKey() );
                ( (CachingBlobStore) blobStore ).invalidate( indexConfigSegment, indexConfigBlobRecord.getKey() );
            }
            throw new RuntimeException(
                "Failed to load blobs with keys: " + nodeBlobRecord.getKey() + ", " + indexConfigBlobRecord.getKey(), e );
        }
    }

    @Reference
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }
}
