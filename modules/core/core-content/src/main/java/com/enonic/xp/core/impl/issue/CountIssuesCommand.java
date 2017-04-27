package com.enonic.xp.core.impl.issue;

import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.IssueQueryNodeQueryTranslator;
import com.enonic.xp.node.NodeQuery;

public class CountIssuesCommand
    extends AbstractIssueCommand
{
    private final IssueQuery query;

    private CountIssuesCommand( final Builder builder )
    {
        super( builder );
        this.query = builder.query;
    }

    public Long execute()
    {
        final NodeQuery nodeQuery = IssueQueryNodeQueryTranslator.translate( this.query );

        return nodeService.findByQuery( nodeQuery ).getTotalHits();
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

        public CountIssuesCommand build()
        {
            return new CountIssuesCommand( this );
        }
    }
}
