package com.enonic.xp.core.impl.issue;

import com.enonic.xp.issue.DeleteIssueCommentParams;
import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.RefreshMode;

public class DeleteIssueCommentCommand
    extends AbstractIssueCommand
{
    private final DeleteIssueCommentParams params;

    private DeleteIssueCommentCommand( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public DeleteIssueCommentResult execute()
    {
        return doExecute();
    }

    private DeleteIssueCommentResult doExecute()
    {
        validateBlockingChecks();

        final DeleteNodeResult deleteNodeResult =
            nodeService.delete( DeleteNodeParams.create().nodeId( params.getComment() ).refresh( RefreshMode.ALL ).build() );

        return new DeleteIssueCommentResult( NodeIds.from( deleteNodeResult.getNodeBranchEntries().getKeys() ) );
    }

    private void validateBlockingChecks()
    {
        if ( params.getComment() == null )
        {
            throw new IllegalArgumentException( "Issue comment id can not be null." );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractIssueCommand.Builder<Builder>
    {

        private DeleteIssueCommentParams params;

        public Builder params( final DeleteIssueCommentParams params )
        {
            this.params = params;
            return this;
        }

        private Builder()
        {
        }

        public DeleteIssueCommentCommand build()
        {
            return new DeleteIssueCommentCommand( this );
        }
    }

}
