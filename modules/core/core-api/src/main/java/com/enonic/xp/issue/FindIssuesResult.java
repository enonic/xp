package com.enonic.xp.issue;


import java.util.List;

import com.google.common.collect.ImmutableList;

public final class FindIssuesResult
{
    private final ImmutableList<Issue> issues;

    private final long totalHits;

    private FindIssuesResult( Builder builder )
    {
        this.issues = ImmutableList.copyOf( builder.issues );
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

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private List<Issue> issues = ImmutableList.of();

        private long totalHits;

        private Builder()
        {
        }

        public Builder issues( final List<Issue> issues )
        {
            this.issues = issues;
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
