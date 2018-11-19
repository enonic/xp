package com.enonic.xp.repo.impl.node.dao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.CachingBlobStore;
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
    public BlobKey store( final NodeVersion nodeVersion, final InternalContext context )
    {
        final BlobRecord blob = doStoreNodeAsBlob( nodeVersion, context );
        return blob.getKey();
    }

    private BlobRecord doStoreNodeAsBlob( final NodeVersion nodeVersion, final InternalContext context )
    {
        final Segment segment = RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.NODE_SEGMENT_LEVEL );
        final String serializedNode = this.nodeVersionJsonSerializer.toString( nodeVersion );
        final ByteSource source = ByteSource.wrap( serializedNode.getBytes( StandardCharsets.UTF_8 ) );
        return blobStore.addRecord( segment, source );
    }

    @Override
    public NodeVersions get( final BlobKeys blobKeys, final InternalContext context )
    {
        return doGetNodeVersions( blobKeys, context );
    }

    @Override
    public NodeVersion get( final BlobKey blobKey, final InternalContext context )
    {
        return doGetNodeVersion( blobKey, context );
    }

    private NodeVersions doGetNodeVersions( final BlobKeys blobKeys, final InternalContext context )
    {
        NodeVersions.Builder builder = NodeVersions.create();

        for ( final BlobKey blobKey : blobKeys )
        {
            builder.add( doGetNodeVersion( blobKey, context ) );
        }

        return builder.build();
    }

    private NodeVersion doGetNodeVersion( final BlobKey blobKey, final InternalContext context )
    {
        return getFromBlob( blobKey, context );
    }

    private NodeVersion getFromBlob( final BlobKey blobKey, final InternalContext context )
    {
        final Segment segment = RepositorySegmentUtils.toSegment( context.getRepositoryId(), NodeConstants.NODE_SEGMENT_LEVEL );
        final BlobRecord blob = blobStore.getRecord( segment, blobKey );

        if ( blob == null )
        {
            throw new IllegalArgumentException( "Cannot get blob with blobKey: " + blobKey + ": blob is null" );
        }

        try
        {
            final String str = blob.getBytes().asCharSource( Charsets.UTF_8 ).read();
            return this.nodeVersionJsonSerializer.toNodeVersion( str );
        }
        catch ( IOException e )
        {
            if ( blobStore instanceof CachingBlobStore )
            {
                ( (CachingBlobStore) blobStore ).invalidate( segment, blob.getKey() );
            }
            throw new RuntimeException( "Failed to load blob with key: " + blob.getKey(), e );
        }
    }

    @Reference
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }
}
