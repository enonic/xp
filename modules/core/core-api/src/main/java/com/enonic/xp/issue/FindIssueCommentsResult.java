package com.enonic.xp.issue;


import java.util.List;

import com.google.common.collect.ImmutableList;

public final class FindIssueCommentsResult
{
    private final ImmutableList<IssueComment> comments;

    private final long totalHits;

    private FindIssueCommentsResult( Builder builder )
    {
        this.comments = ImmutableList.copyOf( builder.comments );
        this.totalHits = builder.totalHits;
    }

    public List<IssueComment> getIssueComments()
    {
        return comments;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private List<IssueComment> comments = ImmutableList.of();

        private long totalHits;

        private Builder()
        {
        }

        public Builder comments( final List<IssueComment> comments )
        {
            this.comments = comments;
            return this;
        }

        public Builder totalHits( final long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public FindIssueCommentsResult build()
        {
            return new FindIssueCommentsResult( this );
        }
    }
}
