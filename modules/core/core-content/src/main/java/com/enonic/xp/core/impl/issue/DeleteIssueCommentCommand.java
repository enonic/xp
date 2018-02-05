package com.enonic.xp.core.impl.issue;

import com.enonic.xp.issue.DeleteIssueCommentParams;
import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
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
        final Node issueNode = nodeService.getById( NodeId.from( params.getIssue() ) );

        final NodePath nodePath = new NodePath( issueNode.path(), params.getComment() );
        NodeIds deletedIds = nodeService.deleteByPath( nodePath );

        nodeService.refresh( RefreshMode.SEARCH );
        return new DeleteIssueCommentResult( deletedIds, nodePath );
    }

    private void validateBlockingChecks()
    {
        if ( params.getIssue() == null )
        {
            throw new IllegalArgumentException( "Issue id can not be null." );
        }
        if ( params.getComment() == null )
        {
            throw new IllegalArgumentException( "Issue comment name can not be null." );
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
