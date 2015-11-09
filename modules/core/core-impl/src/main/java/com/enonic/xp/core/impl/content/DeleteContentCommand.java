package com.enonic.xp.core.impl.content;


import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;


final class DeleteContentCommand
    extends AbstractContentCommand
{
    private final DeleteContentParams params;

    private DeleteContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    Contents execute()
    {
        params.validate();

        try
        {
            final Contents deletedContents = doExecute();
            nodeService.refresh( RefreshMode.SEARCH );
            return deletedContents;
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private Contents doExecute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( this.params.getContentPath() );
        final Node nodeToDelete = this.nodeService.getByPath( nodePath );

        final Nodes.Builder nodesToDelete = Nodes.create();
        recursiveDelete( nodeToDelete, nodesToDelete );

        return this.translator.fromNodes( nodesToDelete.build(), false );
    }

    private void recursiveDelete( final Node nodeToDelete, final Nodes.Builder deletedNodes )
    {
        final CompareStatus status = getCompareStatus( nodeToDelete );

        if ( status == CompareStatus.NEW )
        {
            deletedNodes.addAll( this.getDeletedNodeChildren( nodeToDelete.path() ) );

            final Node deletedNode = nodeService.deleteById( nodeToDelete.id() );
            deletedNodes.add( deletedNode );
        }
        else
        {
            final SetNodeStateResult setNodeStateResult = this.nodeService.setNodeState( SetNodeStateParams.create().
                nodeId( nodeToDelete.id() ).
                nodeState( NodeState.PENDING_DELETE ).
                build() );

            deletedNodes.addAll( setNodeStateResult.getUpdatedNodes() );

            this.nodeService.refresh( RefreshMode.SEARCH );

            final FindNodesByParentResult findNodesByParentResult = this.nodeService.findByParent( FindNodesByParentParams.create().
                parentPath( nodeToDelete.path() ).
                build() );

            for ( Node childNodeToDelete : findNodesByParentResult.getNodes() )
            {
                recursiveDelete( childNodeToDelete, deletedNodes );
            }
        }
    }

    private Nodes getDeletedNodeChildren( final NodePath deletedNodePath )
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate(
            ContentQuery.create().queryExpr( constructExprToFetchChildren( deletedNodePath ) ).build() );

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        return result.getNodes();
    }

    private QueryExpr constructExprToFetchChildren( final NodePath nodePath )
    {
        return QueryExpr.from( CompareExpr.like( FieldExpr.from( "_path" ), ValueExpr.string( nodePath + "/*" ) ) );
    }

    private CompareStatus getCompareStatus( final Node nodeToDelete )
    {
        final Context context = ContextAccessor.current();
        final Branch currentBranch = context.getBranch();

        final NodeComparison compare;
        if ( currentBranch.equals( ContentConstants.BRANCH_DRAFT ) )
        {
            compare = this.nodeService.compare( nodeToDelete.id(), ContentConstants.BRANCH_MASTER );
        }
        else
        {
            compare = this.nodeService.compare( nodeToDelete.id(), ContentConstants.BRANCH_DRAFT );
        }
        return compare.getCompareStatus();
    }


    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private DeleteContentParams params;

        public Builder params( final DeleteContentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            Preconditions.checkNotNull( params );
        }

        public DeleteContentCommand build()
        {
            validate();
            return new DeleteContentCommand( this );
        }
    }

}
