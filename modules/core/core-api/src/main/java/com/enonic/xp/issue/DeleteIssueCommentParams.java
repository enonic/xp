package com.enonic.xp.issue;

import com.enonic.xp.node.NodeName;

public final class DeleteIssueCommentParams
{
    private final IssueId issue;

    private final NodeName comment;

    private DeleteIssueCommentParams( Builder builder )
    {
        this.issue = builder.issue;
        this.comment = builder.comment;
    }

    public IssueId getIssue()
    {
        return issue;
    }

    public NodeName getComment()
    {
        return comment;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IssueId issue;

        private NodeName comment;

        private Builder()
        {
        }

        public Builder issue( final IssueId issue )
        {
            this.issue = issue;
            return this;
        }

        public Builder comment( final NodeName comment )
        {
            this.comment = comment;
            return this;
        }

        public DeleteIssueCommentParams build()
        {
            return new DeleteIssueCommentParams( this );
        }
    }
}
