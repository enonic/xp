package com.enonic.xp.core.impl.issue;

import java.util.List;

import com.enonic.xp.issue.FindIssuesResult;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;

public class FindIssuesCommand
    extends AbstractIssueCommand
{
    private final IssueQuery query;

    private FindIssuesCommand( final Builder builder )
    {
        super( builder );
        this.query = builder.query;
    }

    public FindIssuesResult execute()
    {
        final NodeQuery nodeQuery = IssueQueryNodeQueryTranslator.translate( this.query );

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        final Nodes foundNodes = this.nodeService.getByIds( result.getNodeIds() );

        final List<Issue> issues = IssueNodeTranslator.fromNodes( foundNodes );

        return FindIssuesResult.create().issues( issues ).totalHits( result.getTotalHits() ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractIssueCommand.Builder<Builder>
    {

        private IssueQuery query;

        private Builder()
        {
        }

        public Builder query( final IssueQuery query )
        {
            this.query = query;
            return this;
        }

        public FindIssuesCommand build()
        {
            return new FindIssuesCommand( this );
        }
    }
}
