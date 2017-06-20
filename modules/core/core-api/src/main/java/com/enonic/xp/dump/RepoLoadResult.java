package com.enonic.xp.dump;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.repository.RepositoryId;

public class RepoLoadResult
    implements Iterable<BranchLoadResult>
{
    private final List<BranchLoadResult> branchResults;

    private final RepositoryId repositoryId;

    private final Duration duration;

    private RepoLoadResult( final Builder builder )
    {
        branchResults = builder.branchResults;
        repositoryId = builder.repositoryId;
        this.duration = Duration.ofMillis( builder.endTime - builder.startTime );
    }

    @Override
    public Iterator<BranchLoadResult> iterator()
    {
        return this.branchResults.iterator();
    }

    public List<BranchLoadResult> getBranchResults()
    {
        return branchResults;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Duration getDuration()
    {
        return duration;
    }

    public static Builder create( final RepositoryId repositoryId )
    {
        return new Builder( repositoryId );
    }

    public static final class Builder
    {
        private final List<BranchLoadResult> branchResults = Lists.newArrayList();

        private final RepositoryId repositoryId;

        private final Long startTime;

        private Long endTime;

        private Builder( final RepositoryId repositoryId )
        {
            this.startTime = System.currentTimeMillis();
            this.repositoryId = repositoryId;
        }

        public Builder add( final BranchLoadResult val )
        {
            branchResults.add( val );
            return this;
        }

        public RepoLoadResult build()
        {
            endTime = System.currentTimeMillis();
            return new RepoLoadResult( this );
        }
    }
}
