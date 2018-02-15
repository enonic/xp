package com.enonic.xp.issue;

import com.enonic.xp.node.NodeId;

public final class DeleteIssueCommentParams
{
    private final NodeId comment;

    private DeleteIssueCommentParams( Builder builder )
    {
        this.comment = builder.comment;
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

        private Builder()
        {
        }

        public Builder comment( final NodeId comment )
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
