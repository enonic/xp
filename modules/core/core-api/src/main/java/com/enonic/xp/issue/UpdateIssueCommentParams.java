package com.enonic.xp.issue;

import com.enonic.xp.node.NodeName;

public final class UpdateIssueCommentParams
{
    private final IssueId issue;

    private final NodeName comment;

    private final String text;

    private UpdateIssueCommentParams( Builder builder )
    {
        this.issue = builder.issue;
        this.text = builder.text;
        this.comment = builder.comment;
    }

    public IssueId getIssue()
    {
        return issue;
    }

    public String getText()
    {
        return text;
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

        private String text;

        private Builder()
        {
        }

        public Builder issue( final IssueId issue )
        {
            this.issue = issue;
            return this;
        }

        public Builder text( final String text )
        {
            this.text = text;
            return this;
        }

        public Builder comment( final NodeName comment )
        {
            this.comment = comment;
            return this;
        }

        public UpdateIssueCommentParams build()
        {
            return new UpdateIssueCommentParams( this );
        }
    }
}
