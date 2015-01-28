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

    private NodeName nodeName;

    private final boolean overwriteExisting;

    private MoveNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
        this.newParentPath = builder.newParentPath;
        this.nodeName = builder.newNodeName;
        this.overwriteExisting = builder.overwriteExisting;
    }

    public Node execute()
    {
        final Node existingNode = doGetById( nodeId, false );

        if ( nodeName == null )
        {
            this.nodeName = existingNode.name();
        }

        if ( samePath( existingNode ) )
        {
            return existingNode;
        }

        if ( existingNode.path().equals( newParentPath ) )
        {
            throw new MoveNodeException( "Not allowed to move to child of self" );
        }

        return doMoveNode( newParentPath, nodeName, nodeId, true );
    }

    private boolean samePath( final Node existingNode )
    {
        return existingNode.path().equals( new NodePath( this.newParentPath, nodeName ) );
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

        return persistedNode;
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

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private NodePath newParentPath;

        private NodeName newNodeName;

        private boolean overwriteExisting = false;

        Builder()
        {
            super();
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
        }
    }

}
