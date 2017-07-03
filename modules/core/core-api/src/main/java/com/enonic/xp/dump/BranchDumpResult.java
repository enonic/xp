package com.enonic.xp.dump;


import java.time.Duration;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.branch.Branch;

public class BranchDumpResult
{
    private final Branch branch;

    private final Long numberOfNodes;

    private final Duration duration;

    private final List<DumpError> errors;

    private BranchDumpResult( final Builder builder )
    {
        this.branch = builder.branch;
        this.numberOfNodes = builder.numberOfNodes;
        this.duration = builder.duration != null ? builder.duration : Duration.ofMillis( builder.endTime - builder.startTime );
        this.errors = builder.errors;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public Long getNumberOfNodes()
    {
        return numberOfNodes;
    }

    public Duration getDuration()
    {
        return duration;
    }

    public List<DumpError> getErrors()
    {
        return errors;
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

        private Duration duration;

        private final List<DumpError> errors = Lists.newArrayList();

        private Builder( final Branch branch )
        {
            this.startTime = System.currentTimeMillis();
            this.branch = branch;
        }

        public Builder addedNode()
        {
            numberOfNodes++;
            return this;
        }

        public Builder addedNodes( final long val )
        {
            numberOfNodes = val;
            return this;
        }

        public Builder duration( final Duration duration )
        {
            this.duration = duration;
            return this;
        }

        public Builder error( final DumpError error )
        {
            this.errors.add( error );
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
            ", timeUsed=" + duration +
            '}';
    }

}
