package com.enonic.wem.core.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentAccessException;
import com.enonic.wem.api.content.ContentChangeEvent;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentState;
import com.enonic.wem.api.content.DeleteContentParams;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeAccessException;
import com.enonic.wem.api.node.NodeComparison;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeState;


final class DeleteContentCommand
    extends AbstractContentCommand
{
    private final DeleteContentParams params;

    private DeleteContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    Content execute()
    {
        params.validate();

        try
        {
            final Content deletedContent = doExecute();
            if ( deletedContent != null && deletedContent.getContentState() != ContentState.PENDING_DELETE )
            {
                eventPublisher.publish( ContentChangeEvent.from( ContentChangeEvent.ContentChangeType.DELETE, deletedContent.getPath() ) );
            }
            return deletedContent;
        }
        catch ( NodeAccessException e )
        {
            throw new ContentAccessException( e );
        }
    }

    private Content doExecute()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( this.params.getContentPath() );

        final Node nodeToDelete = this.nodeService.getByPath( nodePath );

        final CompareStatus.Status status = getCompareStatus( nodeToDelete );

        if ( status.equals( CompareStatus.Status.NEW ) )
        {
            final Node deletedNode = nodeService.deleteByPath( nodePath );
            return translator.fromNode( deletedNode );
        }
        else
        {
            final Node pendingDeleteNode = this.nodeService.setNodeState( nodeToDelete.id(), NodeState.PENDING_DELETE );
            return translator.fromNode( pendingDeleteNode );
        }
    }

    private CompareStatus.Status getCompareStatus( final Node nodeToDelete )
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
        return compare.getCompareStatus().getStatus();
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
