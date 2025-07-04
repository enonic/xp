package com.enonic.xp.issue;


import java.util.Collections;
import java.util.List;

public final class FindIssuesResult
{
    private final List<Issue> issues;

    private final long totalHits;

    private final long hits;

    private FindIssuesResult( Builder builder )
    {
        this.issues = builder.issues;
        this.hits = builder.hits;
        this.totalHits = builder.totalHits;
    }

    public List<Issue> getIssues()
    {
        return issues;
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

    public static final class Builder
    {
        private List<Issue> issues = Collections.emptyList();

        private long totalHits;

        private long hits;

        private Builder()
        {
        }

        public Builder issues( final List<Issue> issues )
        {
            this.issues = issues;
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

        public FindIssuesResult build()
        {
            return new FindIssuesResult( this );
        }
    }
}
