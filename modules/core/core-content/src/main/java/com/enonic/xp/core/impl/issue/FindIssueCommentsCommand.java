package com.enonic.xp.core.impl.issue;

import java.util.List;

import com.enonic.xp.issue.FindIssueCommentsResult;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueCommentQuery;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;

public class FindIssueCommentsCommand
    extends AbstractIssueCommand
{
    private final IssueCommentQuery query;

    private FindIssueCommentsCommand( final Builder builder )
    {
        super( builder );
        this.query = builder.query;
    }

    public FindIssueCommentsResult execute()
    {
        validateBlockingChecks();

        final Node issue = nodeService.getById( NodeId.from( this.query.getIssue() ) );

        final NodeQuery nodeQuery = IssueCommentQueryNodeQueryTranslator.translate( this.query, issue.name() );

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        final Nodes foundNodes = this.nodeService.getByIds( result.getNodeIds() );

        final List<IssueComment> issues = IssueCommentNodeTranslator.fromNodes( foundNodes );

        return FindIssueCommentsResult.create().comments( issues ).hits( result.getHits() ).totalHits( result.getTotalHits() ).build();
    }

    private void validateBlockingChecks()
    {
        if ( query.getIssue() == null )
        {
            throw new IllegalArgumentException( "Issue id can not be null." );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractIssueCommand.Builder<Builder>
    {

        private IssueCommentQuery query;

        private Builder()
        {
        }

        public Builder query( final IssueCommentQuery query )
        {
            this.query = query;
            return this;
        }

        public FindIssueCommentsCommand build()
        {
            return new FindIssueCommentsCommand( this );
        }
    }
}
