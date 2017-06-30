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

    private final VersionsLoadResult versionsLoadResult;

    private final RepositoryId repositoryId;

    private final Duration duration;

    private RepoLoadResult( final Builder builder )
    {
        this.branchResults = builder.branchResults;
        this.repositoryId = builder.repositoryId;
        this.versionsLoadResult = builder.versionsLoadResult;
        this.duration = Duration.ofMillis( builder.endTime - builder.startTime );
    }

    @Override
    public Iterator<BranchLoadResult> iterator()
    {
        return this.branchResults.iterator();
    }

    @SuppressWarnings("unused")
    public List<BranchLoadResult> getBranchResults()
    {
        return branchResults;
    }

    @SuppressWarnings("unused")
    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    @SuppressWarnings("unused")
    public Duration getDuration()
    {
        return duration;
    }

    public VersionsLoadResult getVersionsLoadResult()
    {
        return versionsLoadResult;
    }

    public static Builder create( final RepositoryId repositoryId )
    {
        return new Builder( repositoryId );
    }

    public static final class Builder
    {
        private final List<BranchLoadResult> branchResults = Lists.newArrayList();

        private final RepositoryId repositoryId;

        private VersionsLoadResult versionsLoadResult = VersionsLoadResult.create().build();

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

        public Builder versions( final VersionsLoadResult val )
        {
            versionsLoadResult = val;
            return this;
        }

        public RepoLoadResult build()
        {
            endTime = System.currentTimeMillis();
            return new RepoLoadResult( this );
        }
    }
}
