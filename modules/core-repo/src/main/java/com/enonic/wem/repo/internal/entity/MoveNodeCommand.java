package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.MoveNodeException;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.repo.internal.index.query.QueryService;

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
        final Node existingNode = doGetById( nodeId, false );

        final NodeName newNodeName;
        if ( this.newNodeName == null )
        {
            newNodeName = existingNode.name();
        }
        else
        {
            newNodeName = this.newNodeName;
        }

        final NodePath newParentPath;
        if ( this.newParentPath == null )
        {
            newParentPath = existingNode.parentPath();
        }
        else
        {
            newParentPath = this.newParentPath;
        }

        if ( samePath( existingNode, newParentPath, newNodeName ) )
        {
            return existingNode;
        }

        checkNotMovedToSelfOrChild( existingNode, newParentPath );

        return doMoveNode( newParentPath, newNodeName, nodeId, true );
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
            final Node existingNode = getExistingNode( newParentPath, newNodeName );

            if ( existingNode != null )
            {
                if ( !overwriteExisting )
                {
                    throw new MoveNodeException( "Node already exist at path: " + existingNode.path() );
                }
                else
                {
                    nodeName = NodeName.from( DuplicateValueResolver.name( nodeName ) );
                }
            }
        }
        else
        {
            checkExistingNode = false;
        }

        final Node movedNode = Node.newNode( persistedNode ).
            name( nodeName ).
            parentPath( newParentPath ).
            modifiedTime( Instant.now() ).
            modifier( getCurrentPrincipalKey() ).
            indexConfigDocument( persistedNode.getIndexConfigDocument() ).
            build();

        // The node that is moved must be updated
        if ( movedNode.id().equals( this.nodeId ) )
        {
            doStoreNode( movedNode );
        }
        else
        {
            updateNodeMetadata( movedNode );
        }

        if ( persistedNode.getHasChildren() )
        {
            final Nodes children = getChildren( persistedNode );

            for ( final Node child : children )
            {
                doMoveNode( movedNode.path(), child.name(), child.id(), checkExistingNode );
            }
        }

        return movedNode;
    }

    public Node getExistingNode( final NodePath newParentNodePath, final NodeName newNodeName )
    {
        final NodePath newNodePath = NodePath.newNodePath( newParentNodePath, newNodeName.toString() ).build();

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
