package com.enonic.xp.repo.impl.node.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;

import com.google.common.io.ByteStreams;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.blob.Blob;
import com.enonic.xp.repo.impl.blob.BlobKey;
import com.enonic.xp.repo.impl.blob.BlobStore;
import com.enonic.xp.repo.impl.blob.file.FileBlobStore;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.NodeJsonSerializer;

@Component
public class NodeDaoImpl
    implements NodeDao
{
    private final NodeJsonSerializer nodeJsonSerializer = NodeJsonSerializer.create( false );

    private final BlobStore nodeBlobStore = new FileBlobStore( NodeConstants.NODE_BLOB_STORE_DIR );

    @Override
    public NodeVersionId store( final Node node )
    {
        final Blob blob = doStoreNodeAsBlob( node );

        return NodeVersionId.from( blob.getKey().toString() );
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
    public Nodes get( final NodeVersionIds nodeVersionIds )
    {
        return doGetFromVersionIds( nodeVersionIds );
    }

    @Override
    public Node get( final NodeVersionId nodeVersionId )
    {
        return doGetByVersionId( nodeVersionId );
    }

    private Node doGetByVersionId( final NodeVersionId nodeVersionId )
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

            final Node node = this.nodeJsonSerializer.toNode( new String( bytes, StandardCharsets.UTF_8 ) );

            return node;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load blob with key: " + blob.getKey(), e );
        }
    }
}
