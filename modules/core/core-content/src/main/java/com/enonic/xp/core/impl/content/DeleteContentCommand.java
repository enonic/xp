package com.enonic.xp.core.impl.content;


import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;


final class DeleteContentCommand
    extends AbstractContentCommand
{
    private final DeleteContentParams params;

    private DeleteContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
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
        recursiveDelete( nodeToDelete.id(), nodesToDelete );

        this.nodeService.refresh( RefreshMode.ALL );

        return this.translator.fromNodes( nodesToDelete.build(), false );
    }

    private void recursiveDelete( NodeId nodeToDelete, Nodes.Builder deletedNodes )
    {
        final CompareStatus status = getCompareStatus( nodeToDelete );

        if ( status == CompareStatus.NEW )
        {
            final Node deletedNode = nodeService.deleteById( nodeToDelete );
            deletedNodes.add( deletedNode );
            return;
        }

        if ( this.params.isDeleteOnline() )
        {
            final Context currentContext = ContextAccessor.current();
            deleteNodeInContext( nodeToDelete, currentContext );
            final Node deletedNode = deleteNodeInContext( nodeToDelete, ContextBuilder.from( currentContext ).
                branch( ContentConstants.BRANCH_MASTER ).
                build() );
            deletedNodes.add( deletedNode );
            return;
        }

        final SetNodeStateResult setNodeStateResult = this.nodeService.setNodeState( SetNodeStateParams.create().
            nodeId( nodeToDelete ).
            nodeState( NodeState.PENDING_DELETE ).
            build() );

        deletedNodes.addAll( setNodeStateResult.getUpdatedNodes() );

        final FindNodesByParentResult findNodesByParentResult = this.nodeService.findByParent( FindNodesByParentParams.create().
            parentId( nodeToDelete ).
            build() );

        for ( NodeId childNodeToDelete : findNodesByParentResult.getNodeIds() )
        {
            recursiveDelete( childNodeToDelete, deletedNodes );
        }
    }

    private CompareStatus getCompareStatus( final NodeId nodeToDelete )
    {
        final Context context = ContextAccessor.current();
        final Branch currentBranch = context.getBranch();

        final NodeComparison compare;
        if ( currentBranch.equals( ContentConstants.BRANCH_DRAFT ) )
        {
            compare = this.nodeService.compare( nodeToDelete, ContentConstants.BRANCH_MASTER );
        }
        else
        {
            compare = this.nodeService.compare( nodeToDelete, ContentConstants.BRANCH_DRAFT );
        }
        return compare.getCompareStatus();
    }

    private Node deleteNodeInContext( final NodeId nodeToDelete, final Context context )
    {
        return context.callWith( () -> this.nodeService.deleteById( nodeToDelete ) );
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
