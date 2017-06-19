package com.enonic.xp.dump;


import java.time.Duration;

import com.enonic.xp.branch.Branch;

public class BranchDumpResult
{
    private final Branch branch;

    private final Long numberOfNodes;

    private final Long numberOfVersions;

    private final Duration timeUsed;

    private BranchDumpResult( final Builder builder )
    {
        this.branch = builder.branch;
        this.numberOfNodes = builder.numberOfNodes;
        this.numberOfVersions = builder.numberOfVersions;
        this.timeUsed = Duration.ofMillis( builder.endTime - builder.startTime );
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

    public Duration getTimeUsed()
    {
        return timeUsed;
    }

    public static Builder create( final Branch branch )
    {
        return new Builder( branch );
    }

    public static final class Builder
    {
        private final Long startTime;

        private Long endTime;

        private final Branch branch;

        private Long numberOfNodes = 0L;

        private Long numberOfVersions = 0L;

        private Builder( final Branch branch )
        {
            this.startTime = System.currentTimeMillis();
            this.branch = branch;
        }

        public Builder addNode()
        {
            numberOfNodes++;
            return this;
        }

        public Builder addedNodes( final long val )
        {
            numberOfNodes = val;
            return this;
        }

        public Builder addedVersions( final long val )
        {
            numberOfVersions += val;
            return this;
        }

        public BranchDumpResult build()
        {
            this.endTime = System.currentTimeMillis();
            return new BranchDumpResult( this );
        }
    }

    @Override
    public String toString()
    {
        return "BranchDumpResult{" +
            "branch=" + branch +
            ", numberOfNodes=" + numberOfNodes +
            ", numberOfVersions=" + numberOfVersions +
            ", timeUsed=" + timeUsed +
            '}';
    }

}
