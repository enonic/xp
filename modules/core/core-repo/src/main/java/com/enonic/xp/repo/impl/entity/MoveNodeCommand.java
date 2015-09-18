package com.enonic.xp.repo.impl.entity;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.index.query.QueryService;
import com.enonic.xp.security.acl.Permission;

public class MoveNodeCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodePath newParentPath;

    private final NodeName newNodeName;

    private final boolean overwriteExisting;

    private MoveNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
        this.newParentPath = builder.newParentPath;
        this.newNodeName = builder.newNodeName;
        this.overwriteExisting = builder.overwriteExisting;
    }

    public Node execute()
    {
        final Node existingSourceNode = doGetById( nodeId, false );

        final NodeName newNodeName;
        if ( this.newNodeName == null )
        {
            newNodeName = existingSourceNode.name();
        }
        else
        {
            newNodeName = this.newNodeName;
        }

        final NodePath newParentPath;
        if ( this.newParentPath == null )
        {
            newParentPath = existingSourceNode.parentPath();
        }
        else
        {
            newParentPath = this.newParentPath;
        }

        if ( samePath( existingSourceNode, newParentPath, newNodeName ) )
        {
            return existingSourceNode;
        }

        checkNotMovedToSelfOrChild( existingSourceNode, newParentPath );

        checkContextUserPermissionOrAdmin( existingSourceNode, newParentPath );

        return doMoveNode( newParentPath, newNodeName, nodeId, true );
    }

    private void checkNotMovedToSelfOrChild( final Node existingSourceNode, final NodePath newParentPath )
    {
        if ( newParentPath.equals( existingSourceNode.path() ) )
        {
            throw new MoveNodeException(
                "Not allowed to move to " + newParentPath + " because child of self ( " + existingSourceNode.path() );
        }

        if ( newParentPath.getParentPaths().contains( existingSourceNode.path() ) )
        {
            throw new MoveNodeException(
                "Not allowed to move to " + newParentPath + " because child of self ( " + existingSourceNode.path() );
        }
    }

    private void checkContextUserPermissionOrAdmin( final Node existingSourceNode, final NodePath newParentPath )
    {
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.DELETE, existingSourceNode );

        final Node newParentNode = doGetByPath( newParentPath, false );
        if ( newParentNode == null )
        {
            throw new NodeAccessException( ContextAccessor.current().getAuthInfo().getUser(), newParentPath, Permission.READ );
        }
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.CREATE, newParentNode );

    }

    private boolean samePath( final Node existingNode, final NodePath newParentPath, final NodeName newNodeName )
    {
        return existingNode.parentPath().equals( newParentPath ) && existingNode.name().equals( newNodeName );
    }

    protected Node doMoveNode( final NodePath newParentPath, final NodeName newNodeName, final NodeId id, boolean checkExistingNode )
    {
        final Node persistedNode = doGetById( id, true );

        NodeName nodeName = ( newNodeName != null ) ? newNodeName : persistedNode.name();

        if ( checkExistingNode )
        {
            final Node existingTargetNode = getExistingNode( newParentPath, newNodeName );

            if ( existingTargetNode != null )
            {
                if ( !overwriteExisting )
                {
                    throw new NodeAlreadyExistAtPathException( existingTargetNode.path() );
                }
                else
                {
                    nodeName = NodeName.from( DuplicateValueResolver.name( nodeName ) );
                }
            }
        }

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
            movedNode = doStoreNode( nodeToMove );
        }
        else
        {
            movedNode = updateNodeMetadata( nodeToMove );
        }

        if ( persistedNode.getHasChildren() )
        {
            final Nodes children = getChildren( persistedNode );

            for ( final Node child : children )
            {
                doMoveNode( nodeToMove.path(), child.name(), child.id(), checkExistingNode );
            }
        }

        return movedNode;
    }

    public Node getExistingNode( final NodePath newParentNodePath, final NodeName newNodeName )
    {
        final NodePath newNodePath = NodePath.create( newParentNodePath, newNodeName.toString() ).build();

        return doGetByPath( newNodePath, false );
    }

    private Nodes getChildren( final Node parentNode )
    {
        final FindNodesByParentResult result = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( parentNode.path() ).
            size( QueryService.GET_ALL_SIZE_FLAG ).
            build() );

        if ( result.isEmpty() )
        {
            return Nodes.empty();
        }

        return result.getNodes();
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

        private boolean overwriteExisting = false;

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

        public Builder overwriteExisting( final boolean overwriteExisting )
        {
            this.overwriteExisting = overwriteExisting;
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
