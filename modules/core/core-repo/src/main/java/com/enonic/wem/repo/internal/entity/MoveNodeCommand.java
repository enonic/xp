package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.acl.Permission;

public class MoveNodeCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodePath newParentPath;

    private final NodeName newNodeName;

    private MoveNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
        this.newParentPath = builder.newParentPath;
        this.newNodeName = builder.newNodeName;
    }

    public Node execute()
    {
        final Node existingNode = doGetById( nodeId );

        final NodeName newNodeName = resolveNodeName( existingNode );

        final NodePath newParentPath = resolvePath( existingNode );

        if ( noChanges( existingNode, newParentPath, newNodeName ) )
        {
            return existingNode;
        }

        checkNotMovedToSelfOrChild( existingNode, newParentPath );

        checkContextUserPermissionOrAdmin( existingNode, newParentPath );

        final Node movedNode = doMoveNode( newParentPath, newNodeName, nodeId );

        indexServiceInternal.refresh( IndexNameResolver.resolveSearchIndexName( ContextAccessor.current().getRepositoryId() ) );

        return movedNode;
    }

    private void checkContextUserPermissionOrAdmin( final Node existingSourceNode, final NodePath newParentPath )
    {
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.DELETE, existingSourceNode );

        final Node newParentNode = GetNodeByPathCommand.create( this ).
            nodePath( newParentPath ).
            build().
            execute();

        if ( newParentNode == null )
        {
            throw new NodeNotFoundException( "Cannot move node to parent with path '" + newParentPath + "', does not exist" );
        }

        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.CREATE, newParentNode );
    }

    private NodePath resolvePath( final Node existingNode )
    {
        final NodePath newParentPath;
        if ( this.newParentPath == null )
        {
            newParentPath = existingNode.parentPath();
        }
        else
        {
            newParentPath = this.newParentPath;
        }
        return newParentPath;
    }

    private NodeName resolveNodeName( final Node existingNode )
    {
        final NodeName newNodeName;
        if ( this.newNodeName == null )
        {
            newNodeName = existingNode.name();
        }
        else
        {
            newNodeName = this.newNodeName;
        }
        return newNodeName;
    }

    private void checkNotMovedToSelfOrChild( final Node existingNode, final NodePath newParentPath )
    {
        if ( newParentPath.equals( existingNode.path() ) )
        {
            throw new MoveNodeException( "Not allowed to move to " + newParentPath + " because child of self ( " + existingNode.path() );
        }

        if ( newParentPath.getParentPaths().contains( existingNode.path() ) )
        {
            throw new MoveNodeException( "Not allowed to move to " + newParentPath + " because child of self ( " + existingNode.path() );
        }
    }

    private boolean noChanges( final Node existingNode, final NodePath newParentPath, final NodeName newNodeName )
    {
        return existingNode.parentPath().equals( newParentPath ) && existingNode.name().equals( newNodeName );
    }

    private Node doMoveNode( final NodePath newParentPath, final NodeName newNodeName, final NodeId id )
    {
        final Node persistedNode = doGetById( id );

        final FindNodesByParentResult result = FindNodesByParentCommand.create( this ).
            params( FindNodesByParentParams.create().
                parentId( persistedNode.id() ).
                build() ).
            searchService( this.searchService ).
            build().
            execute();

        final NodeName nodeName = ( newNodeName != null ) ? newNodeName : persistedNode.name();

        verifyNoExistingAtNewPath( newParentPath, newNodeName );

        Node nodeToMove = Node.create( persistedNode ).
            name( nodeName ).
            parentPath( newParentPath ).
            indexConfigDocument( persistedNode.getIndexConfigDocument() ).
            timestamp( Instant.now() ).
            build();

        final Node movedNode;

        // The node that is moved must be updated
        if ( nodeToMove.id().equals( this.nodeId ) )
        {
            final boolean isRenaming = newParentPath.equals( persistedNode.parentPath() );
            if ( !isRenaming )
            {
                // when moving a Node "inheritPermissions" must be set to false so the permissions are kept with the transfer
                nodeToMove = Node.create( nodeToMove ).inheritPermissions( false ).build();
            }

            movedNode = StoreNodeCommand.create( this ).
                node( nodeToMove ).
                updateMetadataOnly( false ).
                build().
                execute();
        }
        else
        {
            movedNode = StoreNodeCommand.create( this ).
                updateMetadataOnly( true ).
                node( nodeToMove ).
                build().
                execute();
        }

        for ( final Node child : result.getNodes() )
        {
            doMoveNode( nodeToMove.path(), child.name(), child.id() );
        }

        return movedNode;
    }

    private void verifyNoExistingAtNewPath( final NodePath newParentPath, final NodeName newNodeName )
    {
        final Node nodeAtNewPath = getNodeAtNewPath( newParentPath, newNodeName );

        if ( nodeAtNewPath != null )
        {
            throw new NodeAlreadyExistAtPathException( nodeAtNewPath.path() );
        }
    }

    private Node getNodeAtNewPath( final NodePath newParentNodePath, final NodeName newNodeName )
    {
        final NodePath newNodePath = NodePath.create( newParentNodePath, newNodeName.toString() ).build();

        return GetNodeByPathCommand.create( this ).
            nodePath( newNodePath ).
            build().
            execute();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }


    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private NodePath newParentPath;

        private NodeName newNodeName;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder id( final NodeId nodeId )
        {
            this.id = nodeId;
            return this;
        }

        public Builder newParent( final NodePath parentNodePath )
        {
            this.newParentPath = parentNodePath;
            return this;
        }

        public Builder newNodeName( final NodeName nodeName )
        {
            this.newNodeName = nodeName;
            return this;
        }

        public MoveNodeCommand build()
        {
            validate();
            return new MoveNodeCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( id );

            if ( this.newParentPath == null && this.newNodeName == null )
            {
                throw new IllegalArgumentException( "Must provide either newNodeName or newParentPath" );
            }

        }
    }

}
