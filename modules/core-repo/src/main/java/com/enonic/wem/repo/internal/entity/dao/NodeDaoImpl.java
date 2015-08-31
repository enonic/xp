package com.enonic.wem.repo.internal.entity.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteStreams;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.blob.Blob;
import com.enonic.wem.repo.internal.blob.BlobKey;
import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.blob.file.FileBlobStore;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.entity.NodeConstants;
import com.enonic.wem.repo.internal.entity.json.NodeJsonSerializer;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersion;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RootNode;

@Component
public class NodeDaoImpl
    implements NodeDao
{
    private final NodeJsonSerializer nodeJsonSerializer = NodeJsonSerializer.create( false );

    private final BlobStore nodeBlobStore = new FileBlobStore( NodeConstants.NODE_BLOB_STORE_DIR );

    private BranchService branchService;

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
    public Nodes getByVersionIds( final NodeVersionIds nodeVersionIds )
    {
        return doGetFromVersionIds( nodeVersionIds );
    }

    @Override
    public Node getByVersionId( final NodeVersionId nodeVersionId )
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

            return populateWithMetaData( node );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load blob with key: " + blob.getKey(), e );
        }
    }

    private Node populateWithMetaData( final Node node )
    {
        if ( node instanceof RootNode )
        {
            return node;
        }

        final NodeBranchVersion nodeBranchVersion = this.branchService.get( node.id(), InternalContext.from( ContextAccessor.current() ) );

        if ( nodeBranchVersion == null )
        {
            throw new NodeNotFoundException(
                "Cannot find node with id '" + node.id() + "' in branch: '" + ContextAccessor.current().getBranch().getName() + "'" );
        }

        final NodePath nodePath = nodeBranchVersion.getNodePath();
        final NodePath parentPath = nodePath.getParentPath();
        final NodeName nodeName = NodeName.from( nodePath.getLastElement().toString() );

        return Node.create( node ).
            parentPath( parentPath ).
            name( nodeName ).
            nodeState( nodeBranchVersion.getNodeState() ).
            timestamp( nodeBranchVersion.getTimestamp() ).
            build();
    }

    @Reference
    public void setBranchService( final BranchService branchService )
    {
        this.branchService = branchService;
    }
}
