package com.enonic.wem.core.entity.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.json.NodeJsonSerializer;

public class NodeDaoImpl
    implements NodeDao
{
    private BlobService blobService;

    @Override
    public NodeVersionId store( final Node node )
    {
        final Blob blob = doStoreNodeAsBlob( node );

        return NodeVersionId.from( blob.getKey() );
    }

    private Blob doStoreNodeAsBlob( final Node newNode )
    {
        final String serializedNode = NodeJsonSerializer.toString( newNode );

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( serializedNode.getBytes() );
        return blobService.create( byteArrayInputStream );
    }

    @Override
    public Nodes getByVersionIds( final NodeVersionIds nodeVersionIds )
    {
        return doGetFromVersionIds( nodeVersionIds );
    }

    @Override
    public Node getByVersionId( final NodeVersionId nodeVersionId )
    {
        final BlobKey blobKey = new BlobKey( nodeVersionId.toString() );

        return getNodeFromBlob( blobService.get( blobKey ) );
    }

    private Nodes doGetFromVersionIds( final NodeVersionIds nodeVersionIds )
    {
        final Nodes.Builder nodesBuilder = Nodes.create();

        for ( final NodeVersionId nodeVersionId : nodeVersionIds )
        {
            final Blob blob = blobService.get( new BlobKey( nodeVersionId.toString() ) );

            if ( blob == null )
            {
                throw new NodeNotFoundException( "Blob for node with BlobKey " + nodeVersionId + " not found" );
            }

            nodesBuilder.add( getNodeFromBlob( blob ) );
        }

        return nodesBuilder.build();
    }

    private Node getNodeFromBlob( final Blob blob )
    {
        if ( blob == null )
        {
            throw new IllegalArgumentException( "Trying to load blob when blob is null" );
        }

        try
        {
            final byte[] bytes = ByteStreams.toByteArray( blob.getStream() );

            return NodeJsonSerializer.toNode( new String( bytes ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load blob with key" + blob.getKey() );
        }
    }


    @Inject
    public void setBlobService( final BlobService blobService )
    {
        this.blobService = blobService;
    }

}