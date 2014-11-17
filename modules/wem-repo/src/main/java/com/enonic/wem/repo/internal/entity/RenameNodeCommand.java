package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeAlreadyExistException;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.RenameNodeParams;

public final class RenameNodeCommand
    extends AbstractNodeCommand
{
    private final RenameNodeParams params;

    private RenameNodeCommand( Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    public Node execute()
    {
        final NodeId nodeId = params.getNodeId();

        final Node nodeToBeRenamed = doGetById( nodeId, true );

        final NodePath parentPath = verifyNodeNotExistAtNewPath( nodeToBeRenamed );

        final Node renamedNode = doMoveNode( parentPath, params.getNewNodeName(), params.getNodeId() );

        if ( nodeToBeRenamed.getHasChildren() )
        {
            final Nodes children = getChildren( nodeToBeRenamed );
            moveNodesToNewParentPath( children, renamedNode.path() );
        }

        return NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( renamedNode );
    }

    private NodePath verifyNodeNotExistAtNewPath( final Node nodeToBeRenamed )
    {
        final NodePath parentPath = nodeToBeRenamed.parent().asAbsolute();
        final NodePath targetPath = new NodePath( parentPath, params.getNewNodeName() );
        final Node existingNodeAtTargetPath = doGetByPath( targetPath, false );

        if ( ( existingNodeAtTargetPath != null ) && !nodeToBeRenamed.id().equals( existingNodeAtTargetPath.id() ) )
        {
            throw new NodeAlreadyExistException( targetPath );
        }

        return parentPath;
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

    private Node doMoveNode( final NodePath newParentPath, final NodeName newNodeName, final NodeId id )
    {
        final Node persistedNode = doGetById( id, false );

        if ( persistedNode.path().equals( new NodePath( newParentPath, newNodeName ) ) )
        {
            return persistedNode;
        }

        final Instant now = Instant.now();

        final Node movedNode = Node.newNode( persistedNode ).
            name( newNodeName ).
            parent( newParentPath ).
            modifiedTime( now ).
            modifier( PrincipalKey.from( "system:user:admin" ) ).
            indexConfigDocument( persistedNode.getIndexConfigDocument() ).
            build();

        doStoreNode( movedNode );
        return movedNode;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        Builder()
        {
            super();
        }

        private RenameNodeParams params;

        public Builder params( RenameNodeParams params )
        {
            this.params = params;
            return this;
        }

        public RenameNodeCommand build()
        {
            return new RenameNodeCommand( this );
        }

    }
}
