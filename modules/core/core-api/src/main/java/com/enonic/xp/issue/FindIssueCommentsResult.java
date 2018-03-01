package com.enonic.xp.issue;


import java.util.Collections;
import java.util.List;

public final class FindIssueCommentsResult
{
    private final List<IssueComment> comments;

    private final long totalHits;

    private final long hits;

    private FindIssueCommentsResult( Builder builder )
    {
        this.comments = builder.comments;
        this.hits = builder.hits;
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

    public long getHits()
    {
        return hits;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<IssueComment> comments = Collections.emptyList();

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder comments( final List<IssueComment> comments )
        {
            this.comments = comments;
            return this;
        }

        public Builder hits( final long hits )
        {
            this.hits = hits;
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
