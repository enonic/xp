package com.enonic.xp.repo.impl.dump;


import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public class BranchDumpResult
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final Long numberOfNodes;

    private final Long numberOfVersions;

    private final Duration timeUsed;

    private BranchDumpResult( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.branch = builder.branch;
        this.numberOfNodes = builder.numberOfNodes;
        this.numberOfVersions = builder.numberOfVersions;
        this.timeUsed = Duration.ofMillis( builder.timer.elapsed( TimeUnit.MILLISECONDS ) );
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public Long getNumberOfNodes()
    {
        return numberOfNodes;
    }

    public Long getNumberOfVersions()
    {
        return numberOfVersions;
    }

    public static Builder create( final Branch branch )
    {
        return new Builder( branch );
    }

    public static final class Builder
    {
        private final Stopwatch timer;

        private RepositoryId repositoryId;

        private final Branch branch;

        private Long numberOfNodes = 0L;

        private Long numberOfVersions = 0L;

        private Builder( final Branch branch )
        {
            this.timer = Stopwatch.createStarted();
            this.branch = branch;
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }


        public Builder metaWritten()
        {
            numberOfNodes++;
            return this;
        }

        public Builder addedVersions( final long val )
        {
            numberOfVersions += val;
            return this;
        }

        public BranchDumpResult build()
        {
            timer.stop();
            return new BranchDumpResult( this );
        }
    }

    @Override
    public String toString()
    {
        return "BranchDumpResult{" +
            "repositoryId=" + repositoryId +
            ", branch=" + branch +
            ", numberOfNodes=" + numberOfNodes +
            ", numberOfVersions=" + numberOfVersions +
            ", timeUsed=" + timeUsed +
            '}';
    }

}
