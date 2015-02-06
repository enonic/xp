package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeState;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.repo.internal.branch.BranchContext;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.QueryService;

abstract class AbstractDeleteNodeCommand
    extends AbstractNodeCommand
{
    protected AbstractDeleteNodeCommand( final Builder builder )
    {
        super( builder );
    }

    void deleteNodeWithChildren( final Node node, final Context context )
    {
        final NodeVersionId nodeVersionId = this.queryService.get( node.id(), IndexContext.from( context ) );

        final FindNodesByParentResult result = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( node.path() ).
            size( QueryService.GET_ALL_SIZE_FLAG ).
            build() );

        for ( final Node child : result.getNodes() )
        {
            deleteNodeWithChildren( child, context );
        }

       /* final Node deletedNode = Node.newNode( node ).
            nodeState( NodeState.PENDING_DELETE ).
            build();

        setAsPendingDelete( context, nodeVersionId, deletedNode );
        */

        doDelete( context, node, nodeVersionId );
    }

    private void setAsPendingDelete( final Context context, final NodeVersionId nodeVersionId, final Node node )
    {
        final Node pendingNode = Node.newNode( node ).
            nodeState( NodeState.PENDING_DELETE ).
            build();

        branchService.store( StoreBranchDocument.create().
            nodeVersionId( nodeVersionId ).
            node( pendingNode ).
            build(), BranchContext.from( context ) );

        indexService.store( pendingNode, nodeVersionId, IndexContext.from( context ) );
    }

    private void doDelete( final Context context, final Node node, final NodeVersionId nodeVersionId )
    {
        branchService.delete( node.id(), BranchContext.from( context ) );

        indexService.delete( node.id(), IndexContext.from( context ) );
    }

    public static class Builder<B extends Builder>
        extends AbstractNodeCommand.Builder<B>
    {
        public Builder()
        {
            super();
        }

        public Builder( final AbstractNodeCommand source )
        {
            super( source );
        }
    }
}
