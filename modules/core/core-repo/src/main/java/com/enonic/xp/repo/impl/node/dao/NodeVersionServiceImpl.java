package com.enonic.xp.repo.impl.node.dao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;

@Component
public class NodeVersionServiceImpl
    implements NodeVersionService
{
    private final NodeVersionJsonSerializer nodeVersionJsonSerializer = NodeVersionJsonSerializer.create( false );

    private BlobStore blobStore;

    @Override
    public NodeVersionId store( final Node node )
    {
        final BlobRecord blob = doStoreNodeAsBlob( node );

        return NodeVersionId.from( blob.getKey().toString() );
    }

    private BlobRecord doStoreNodeAsBlob( final Node node )
    {
        final String serializedNode = this.nodeVersionJsonSerializer.toString( NodeVersion.from( node ) );
        final ByteSource source = ByteSource.wrap( serializedNode.getBytes( StandardCharsets.UTF_8 ) );
        return blobStore.addRecord( NodeConstants.NODE_SEGMENT, source );
    }

    @Override
    public NodeVersions get( final NodeVersionIds nodeVersionIds )
    {
        return doGetFromVersionIds( nodeVersionIds );
    }

    @Override
    public NodeVersion get( final NodeVersionId nodeVersionId )
    {
        return doGetByVersionId( nodeVersionId );
    }

    private NodeVersion doGetByVersionId( final NodeVersionId nodeVersionId )
    {
        final BlobKey blobKey = BlobKey.from( nodeVersionId.toString() );

        final NodeVersion nodeVersionFromBlob = getNodeVersionFromBlob( blobStore.getRecord( NodeConstants.NODE_SEGMENT, blobKey ) );
        return NodeVersion.create( nodeVersionFromBlob ).
            versionId( nodeVersionId ).
            build();
    }

    private NodeVersions doGetFromVersionIds( final NodeVersionIds nodeVersionIds )
    {
        NodeVersions.Builder builder = NodeVersions.create();

        for ( final NodeVersionId nodeVersionId : nodeVersionIds )
        {
            final BlobRecord blob = blobStore.getRecord( NodeConstants.NODE_SEGMENT, BlobKey.from( nodeVersionId.toString() ) );

            if ( blob == null )
            {
                throw new NodeNotFoundException( "Blob for node with BlobKey " + nodeVersionId + " not found" );
            }

            final NodeVersion nodeVersionFromBlob = getNodeVersionFromBlob( blob );
            final NodeVersion nodeVersion = NodeVersion.create( nodeVersionFromBlob ).
                versionId( nodeVersionId ).
                build();

            builder.add( nodeVersion );
        }

        return builder.build();
    }

    private NodeVersion getNodeVersionFromBlob( final BlobRecord blob )
    {
        if ( blob == null )
        {
            throw new IllegalArgumentException( "Trying to load blob when blob is null" );
        }

        try
        {
            final String str = blob.getBytes().asCharSource( Charsets.UTF_8 ).read();
            return this.nodeVersionJsonSerializer.toNodeVersion( str );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load blob with key: " + blob.getKey(), e );
        }
    }

    @Reference
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }
}
