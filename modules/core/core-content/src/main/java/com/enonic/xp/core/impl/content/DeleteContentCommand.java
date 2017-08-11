package com.enonic.xp.core.impl.content;


import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.SetNodeStateParams;


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

    DeleteContentsResult execute()
    {
        params.validate();

        try
        {
            final DeleteContentsResult deletedContents = doExecute();
            nodeService.refresh( RefreshMode.SEARCH );
            return deletedContents;
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private DeleteContentsResult doExecute()
    {
        this.nodeService.refresh( RefreshMode.ALL );

        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( this.params.getContentPath() );
        final Node nodeToDelete = this.nodeService.getByPath( nodePath );

        if ( nodeToDelete == null )
        {
            throw new ContentNotFoundException( this.params.getContentPath(), ContextAccessor.current().getBranch() );
        }

        final DeleteContentsResult deletedContents = doDeleteContent( nodeToDelete.id() );

        this.nodeService.refresh( RefreshMode.ALL );

        return deletedContents;
    }

    private DeleteContentsResult doDeleteContent( NodeId nodeToDelete )
    {
        final CompareStatus rootNodeStatus = getCompareStatus( nodeToDelete );

        final DeleteContentsResult.Builder result = DeleteContentsResult.create();

        if ( rootNodeStatus == CompareStatus.NEW )
        {
            // Root node is new, just delete all children
            final NodeIds nodes = this.nodeService.deleteById( nodeToDelete );

            result.addDeleted( ContentIds.from( nodes.getAsStrings() ) );

        }
        else if ( this.params.isDeleteOnline() )
        {
            final NodeIds nodes = deleteNodeInDraftAndMaster( nodeToDelete );

            result.addDeleted( ContentIds.from( nodes.getAsStrings() ) );
        }
        else
        {
            this.nodeService.setNodeState( SetNodeStateParams.create().
                nodeId( nodeToDelete ).
                nodeState( NodeState.PENDING_DELETE ).
                build() );

            result.addPending( ContentId.from( nodeToDelete.toString() ) );

            final NodeIds children = getAllChildren( nodeToDelete );

            for ( final NodeId child : children )
            {
                final DeleteContentsResult childDeleteResult = this.doDeleteContent( child );

                result.addDeleted( childDeleteResult.getDeletedContents() );
                result.addPending( childDeleteResult.getPendingContents() );
            }
        }
        return result.build();
    }

    private NodeIds deleteNodeInDraftAndMaster( final NodeId nodeToDelete )
    {
        final Context currentContext = ContextAccessor.current();
        final NodeIds draftNodes = deleteNodeInContext( nodeToDelete, currentContext );
        final NodeIds masterNodes = deleteNodeInContext( nodeToDelete, ContextBuilder.from( currentContext ).
            branch( ContentConstants.BRANCH_MASTER ).
            build() );
        return masterNodes != null ? masterNodes : draftNodes;
    }

    private NodeIds getAllChildren( final NodeId nodeToDelete )
    {
        final FindNodesByParentResult findNodesByParentResult = this.nodeService.findByParent( FindNodesByParentParams.create().
            parentId( nodeToDelete ).
            recursive( false ).
            build() );

        return findNodesByParentResult.getNodeIds();
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

    private NodeIds deleteNodeInContext( final NodeId nodeToDelete, final Context context )
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
