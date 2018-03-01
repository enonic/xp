package com.enonic.xp.issue;

import com.enonic.xp.node.NodeId;

public final class UpdateIssueCommentParams
{
    private final NodeId comment;

    private final String text;

    private UpdateIssueCommentParams( Builder builder )
    {
        this.text = builder.text;
        this.comment = builder.comment;
    }

    public String getText()
    {
        return text;
    }

    public NodeId getComment()
    {
        return comment;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private NodeId comment;

        private String text;

        private Builder()
        {
        }

        public Builder text( final String text )
        {
            this.text = text;
            return this;
        }

        public Builder comment( final NodeId comment )
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
