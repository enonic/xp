package com.enonic.xp.core.impl.content;


import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAccessException;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentState;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.Nodes;
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

    Contents execute()
    {
        params.validate();

        try
        {
            final Contents deletedContents = doExecute();
            for ( Content deletedContent : deletedContents )
            {
                if ( deletedContent != null )
                {
                    if ( deletedContent.getContentState() == ContentState.PENDING_DELETE )
                    {
                        eventPublisher.publish(
                            ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.PENDING, deletedContent.getPath() ) );
                    }
                    else
                    {
                        eventPublisher.publish(
                            ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.DELETE, deletedContent.getPath() ) );
                    }
                }
            }
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

        final CompareStatus status = getCompareStatus( nodeToDelete );

        if ( status == CompareStatus.NEW )
        {
            final Node deletedNode = nodeService.deleteByPath( nodePath );
            return translator.fromNodes( Nodes.from( deletedNode ) );
        }
        else
        {
            SetNodeStateParams setNodeStateParams =
                SetNodeStateParams.create().nodeId( nodeToDelete.id() ).nodeState( NodeState.PENDING_DELETE ).recursive( true ).build();
            final SetNodeStateResult setNodeStateResult = this.nodeService.setNodeState( setNodeStateParams );

            return translator.fromNodes( setNodeStateResult.getUpdatedNodes() );
        }
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
