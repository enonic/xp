package com.enonic.wem.repo.internal.entity.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeNotFoundException;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.NodeVersionIds;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.repo.internal.blob.Blob;
import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.blob.file.FileBlobStore;
import com.enonic.wem.repo.internal.entity.NodeConstants;
import com.enonic.wem.repo.internal.entity.json.NodeJsonSerializer;

public class NodeDaoImpl
    implements NodeDao
{
    private final NodeJsonSerializer nodeJsonSerializer = NodeJsonSerializer.create( false );

    private final BlobStore nodeBlobStore = new FileBlobStore( NodeConstants.nodeBlobStoreDir );

    @Override
    public NodeVersionId store( final Node node )
    {
        final Blob blob = doStoreNodeAsBlob( node );

        return NodeVersionId.from( blob.getKey() );
    }

    private Blob doStoreNodeAsBlob( final Node newNode )
    {
        final String serializedNode = this.nodeJsonSerializer.toString( newNode );

        try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
            serializedNode.getBytes( StandardCharsets.UTF_8 ) ))
        {
            return nodeBlobStore.addRecord( byteArrayInputStream );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to create blob for node: " + newNode.toString(), e );
        }
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

        return getNodeFromBlob( nodeBlobStore.getRecord( blobKey ) );
    }

    private Nodes doGetFromVersionIds( final NodeVersionIds nodeVersionIds )
    {
        final Nodes.Builder nodesBuilder = Nodes.create();

        for ( final NodeVersionId nodeVersionId : nodeVersionIds )
        {
            final Blob blob = nodeBlobStore.getRecord( new BlobKey( nodeVersionId.toString() ) );

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

        try (final InputStream stream = blob.getStream())
        {
            final byte[] bytes = ByteStreams.toByteArray( stream );

            return this.nodeJsonSerializer.toNode( new String( bytes, StandardCharsets.UTF_8 ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load blob with key: " + blob.getKey(), e );
        }
    }
}
