package com.enonic.wem.repo.internal.entity.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeNotFoundException;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.NodeVersionIds;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.repo.internal.blob.Blob;
import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.blob.file.FileBlobStore;
import com.enonic.wem.repo.internal.entity.NodeConstants;
import com.enonic.wem.repo.internal.entity.json.NodeJsonSerializer;
import com.enonic.wem.repo.internal.index.query.NodeWorkspaceVersion;
import com.enonic.wem.repo.internal.workspace.WorkspaceContext;
import com.enonic.wem.repo.internal.workspace.WorkspaceService;

public class NodeDaoImpl
    implements NodeDao
{
    private final NodeJsonSerializer nodeJsonSerializer = NodeJsonSerializer.create( false );

    private final BlobStore nodeBlobStore = new FileBlobStore( NodeConstants.nodeBlobStoreDir );

    private WorkspaceService workspaceService;

    @Override
    public NodeVersionId store( final Node node )
    {
        final Blob blob = doStoreNodeAsBlob( node );

        return NodeVersionId.from( blob.getKey() );
    }

    private Blob doStoreNodeAsBlob( final Node newNode )
    {
        NodeId parentId = resolveParentId( newNode );

        final Node populatedNode = Node.newNode( newNode ).
            parentId( parentId ).
            build();

        final String serializedNode = this.nodeJsonSerializer.toString( populatedNode );

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

    private NodeId resolveParentId( final Node newNode )
    {
        NodeId parentId = newNode.getParentId();

        if ( parentId == null && !newNode.parentPath().equals( NodePath.ROOT ) )
        {
            final NodePath parentPath = newNode.path().getParentPath();

            final NodeWorkspaceVersion nodeWorkspaceVersion =
                this.workspaceService.get( parentPath, WorkspaceContext.from( ContextAccessor.current() ) );

            final Node parentNode = doGetByVersionId( nodeWorkspaceVersion.getVersionId() );

            parentId = parentNode.id();
        }
        return parentId;
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

            return populateWithNodeParentPath( node );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load blob with key: " + blob.getKey(), e );
        }
    }

    private Node populateWithNodeParentPath( final Node node )
    {
        final NodeId parentId = node.getParentId();

        if ( parentId != null )
        {
            final NodeWorkspaceVersion nodeWorkspaceVersion =
                this.workspaceService.get( parentId, WorkspaceContext.from( ContextAccessor.current() ) );

            final Node.Builder populatedNode = Node.newNode( node ).
                parentPath( nodeWorkspaceVersion.getNodePath() );

            return populatedNode.build();
        }
        else
        {
            return Node.newNode( node ).
                parentPath( NodePath.ROOT ).
                build();
        }
    }

    public void setWorkspaceService( final WorkspaceService workspaceService )
    {
        this.workspaceService = workspaceService;
    }
}
