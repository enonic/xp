package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.repo.internal.index.query.QueryService;

public class MoveNodeCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodePath newParentNodePath;

    private final NodeName nodeName;

    private MoveNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
        this.newParentNodePath = builder.newParentNodePath;
        this.nodeName = builder.nodeName;
    }

    public Node execute()
    {
        final Node movedNode = doMoveNode( newParentNodePath, nodeName, nodeId );
        return movedNode;
    }

    protected Node doMoveNode( final NodePath newParentPath, final NodeName newNodeName, final NodeId id )
    {
        final Node persistedNode = doGetById( id, true );
        NodeName nodeName = ( newNodeName != null ) ? newNodeName : persistedNode.name();

        if ( persistedNode.path().equals( new NodePath( newParentPath, nodeName ) ) )
        {
            return persistedNode;
        }

        final Instant now = Instant.now();

        final Node movedNode = Node.newNode( persistedNode ).
            name( nodeName ).
            parent( newParentPath ).
            modifiedTime( now ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            indexConfigDocument( persistedNode.getIndexConfigDocument() ).
            build();

        doStoreNode( movedNode );

        if ( persistedNode.getHasChildren() )
        {
            final Nodes children = getChildren( persistedNode );
            moveNodesToNewParentPath( children, movedNode.path() );
        }

        return persistedNode;
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

    private void moveNodesToNewParentPath( final Nodes nodes, final NodePath newParentPath )
    {
        for ( final Node childNodeBeforeMove : nodes )
        {
            final Node movedNode = doMoveNode( newParentPath, childNodeBeforeMove.name(), childNodeBeforeMove.id() );

            final FindNodesByParentResult result = doFindNodesByParent( FindNodesByParentParams.create().
                parentPath( childNodeBeforeMove.path() ).
                size( QueryService.GET_ALL_SIZE_FLAG ).
                build() );

            if ( !result.isEmpty() )
            {
                moveNodesToNewParentPath( result.getNodes(), movedNode.path().asAbsolute() );
            }
        }
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private NodePath newParentNodePath;

        private NodeName nodeName;

        Builder()
        {
            super();
        }

        public Builder id( final NodeId nodeId )
        {
            this.id = nodeId;
            return this;
        }

        public Builder parentNodePath( final NodePath parentNodePath )
        {
            this.newParentNodePath = parentNodePath;
            return this;
        }

        public Builder nodeName( final NodeName nodeName )
        {
            this.nodeName = nodeName;
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
