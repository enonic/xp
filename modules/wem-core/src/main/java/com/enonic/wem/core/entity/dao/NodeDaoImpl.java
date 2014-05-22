package com.enonic.wem.core.entity.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.joda.time.Instant;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobKeys;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.entity.json.NodeJsonSerializer;
import com.enonic.wem.core.workspace.WorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceDocumentFactory;
import com.enonic.wem.core.workspace.WorkspaceStore;
import com.enonic.wem.core.workspace.query.WorkspaceDeleteQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdsQuery;
import com.enonic.wem.core.workspace.query.WorkspaceParentQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathsQuery;

public class NodeDaoImpl
    implements NodeDao
{
    @Inject
    private BlobService blobService;

    @Inject
    private WorkspaceStore workspaceStore;

    @Override
    public Node create( final CreateNodeArguments createNodeArguments, final Workspace workspace )
    {
        final Node newNode = createNodeFromArguments( createNodeArguments );

        final Blob blob = doStoreNodeAsBlob( newNode );

        final BlobKey storedBlobKey = blob.getKey();

        final WorkspaceDocument workspaceDocument = WorkspaceDocumentFactory.create( storedBlobKey, workspace, newNode );

        workspaceStore.store( workspaceDocument );

        return newNode;
    }

    private Blob doStoreNodeAsBlob( final Node newNode )
    {
        final String serializedNode = NodeJsonSerializer.toString( newNode );

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( serializedNode.getBytes() );
        return blobService.create( byteArrayInputStream );
    }

    private Node createNodeFromArguments( final CreateNodeArguments createNodeArguments )
    {
        Preconditions.checkNotNull( createNodeArguments.parent(), "Path of parent Node must be specified" );
        Preconditions.checkArgument( createNodeArguments.parent().isAbsolute(),
                                     "Path to parent Node must be absolute: " + createNodeArguments.parent() );

        final Instant now = Instant.now();

        return Node.newNode().
            id( new EntityId() ).
            createdTime( now ).
            modifiedTime( now ).
            creator( createNodeArguments.creator() ).
            modifier( createNodeArguments.creator() ).
            parent( createNodeArguments.parent() ).
            name( NodeName.from( createNodeArguments.name() ) ).
            rootDataSet( createNodeArguments.rootDataSet() ).
            attachments( createNodeArguments.attachments() ).
            entityIndexConfig( createNodeArguments.entityIndexConfig() ).
            build();
    }


    @Override
    public Nodes getByParent( final NodePath parent, final Workspace workspace )
    {
        final BlobKeys blobKeys = workspaceStore.getByParent( new WorkspaceParentQuery( workspace, parent ) );
        return getNodesFromBlobKeys( blobKeys );
    }


    @Override
    public Nodes getByPaths( final NodePaths paths, final Workspace workspace )
    {
        final BlobKeys blobKeys = workspaceStore.getByPaths( new WorkspacePathsQuery( workspace, paths ) );
        return getNodesFromBlobKeys( blobKeys );
    }

    @Override
    public Node getByPath( final NodePath path, final Workspace workspace )
    {
        return doGetByPath( path, workspace );
    }

    private Node doGetByPath( final NodePath path, final Workspace workspace )
    {
        final BlobKey blobKey = workspaceStore.getByPath( new WorkspacePathQuery( workspace, path ) );
        return getNodeFromBlob( blobService.get( blobKey ) );
    }

    @Override
    public Nodes getByIds( final EntityIds entityIds, final Workspace workspace )
    {
        final BlobKeys blobKeys = workspaceStore.getByIds( new WorkspaceIdsQuery( workspace, entityIds ) );
        return getNodesFromBlobKeys( blobKeys );
    }

    @Override
    public Node getById( final EntityId entityId, final Workspace workspace )
    {
        return doGetById( entityId, workspace );
    }

    private Node doGetById( final EntityId entityId, final Workspace workspace )
    {
        final BlobKey blobKey = workspaceStore.getById( new WorkspaceIdQuery( workspace, entityId ) );
        return getNodeFromBlob( blobService.get( blobKey ) );
    }

    @Override
    public Node deleteById( final EntityId entityId, final Workspace workspace )
    {
        final Node node = getById( entityId, workspace );

        if ( node == null )
        {
            throw new NodeNotFoundException( "Unable to delete node with id " + entityId + " in workspace " + workspace.getName() );
        }

        doDeleteNodeWithChildren( node, workspace );

        return node;
    }

    private void doDeleteNodeWithChildren( final Node nodeToDelete, final Workspace workspace )
    {
        final Nodes children = this.getByParent( nodeToDelete.path(), workspace );

        for ( final Node child : children )
        {
            doDeleteNodeWithChildren( child, workspace );
        }

        workspaceStore.delete( new WorkspaceDeleteQuery( workspace, nodeToDelete.id() ) );
    }


    @Override
    public boolean move( final MoveNodeArguments moveNodeArguments, final Workspace workspace )
    {
        final Node persistedNode = getById( moveNodeArguments.nodeToMove(), workspace );

        if ( persistedNode.path().equals( new NodePath( moveNodeArguments.parentPath(), moveNodeArguments.name() ) ) )
        {
            return false;
        }

        final Instant now = Instant.now();

        final Node movedNode = Node.newNode( persistedNode ).
            id( persistedNode.id() ).
            name( moveNodeArguments.name() ).
            parent( moveNodeArguments.parentPath() ).
            creator( persistedNode.creator() ).
            createdTime( persistedNode.getCreatedTime() ).
            modifiedTime( now ).
            modifier( moveNodeArguments.updater() ).
            rootDataSet( persistedNode.data() ).
            attachments( persistedNode.attachments() ).
            entityIndexConfig( moveNodeArguments.getEntityIndexConfig() != null
                                   ? moveNodeArguments.getEntityIndexConfig()
                                   : persistedNode.getEntityIndexConfig() ).
            build();

        final Blob blob = doStoreNodeAsBlob( movedNode );

        final WorkspaceDocument workspaceDocument = WorkspaceDocumentFactory.create( blob.getKey(), workspace, movedNode );

        workspaceStore.store( workspaceDocument );

        return true;
    }

    @Override
    public Node update( final UpdateNodeArgs updateNodeArguments, final Workspace workspace )
    {
        Preconditions.checkNotNull( updateNodeArguments.nodeToUpdate(), "nodeToUpdate must be specified" );

        final Node persistedNode = this.getById( updateNodeArguments.nodeToUpdate(), workspace );

        final Instant now = Instant.now();

        // TODO: Should'nt new Node(Node) Handle THIS?!

        final Node.Builder updateNodeBuilder = Node.newNode( persistedNode ).
            id( persistedNode.id() ).
            parent( persistedNode.parent() ).
            name( persistedNode.name() ).
            creator( persistedNode.creator() ).
            createdTime( persistedNode.getCreatedTime() ).
            modifiedTime( now ).
            modifier( updateNodeArguments.updater() ).
            rootDataSet( updateNodeArguments.rootDataSet() ).
            attachments( syncronizeAttachments( updateNodeArguments, persistedNode ) ).
            entityIndexConfig( updateNodeArguments.entityIndexConfig() != null
                                   ? updateNodeArguments.entityIndexConfig()
                                   : persistedNode.getEntityIndexConfig() );

        final Node updatedNode = updateNodeBuilder.build();

        final Blob blob = doStoreNodeAsBlob( updatedNode );

        final WorkspaceDocument workspaceDocument = WorkspaceDocumentFactory.create( blob.getKey(), workspace, updatedNode );

        workspaceStore.store( workspaceDocument );

        return updatedNode;
    }

    private Attachments syncronizeAttachments( final UpdateNodeArgs updateNodeArgs, final Node persistedNode )
    {

        final Attachments persistedAttachments = persistedNode.attachments();

        if ( updateNodeArgs.attachments() == null )
        {
            return persistedAttachments;
        }

        return updateNodeArgs.attachments();

    }

    private Nodes getNodesFromBlobKeys( final BlobKeys blobKeys )
    {
        final Nodes.Builder nodesBuilder = Nodes.newNodes();

        for ( final BlobKey blobKey : blobKeys )
        {
            final Blob blob = blobService.get( blobKey );
            nodesBuilder.add( getNodeFromBlob( blob ) );
        }

        return nodesBuilder.build();
    }

    private Node getNodeFromBlob( final Blob blob )
    {
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


}
