package com.enonic.xp.dump;

import java.time.Duration;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.branch.Branch;

public class BranchLoadResult
    implements LoadResult
{
    private final Branch branch;

    private final Long numberOfNodes;

    private final Long numberOfVersions;

    private final Duration duration;

    private final List<LoadError> errors;

    private BranchLoadResult( final Builder builder )
    {
        this.branch = builder.branch;
        this.numberOfNodes = builder.numberOfNodes;
        this.numberOfVersions = builder.numberOfVersions;
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

    public Long getNumberOfVersions()
    {
        return numberOfVersions;
    }

    public Duration getDuration()
    {
        return duration;
    }

    public List<LoadError> getErrors()
    {
        return errors;
    }

    public static Builder create( final Branch branch )
    {
        return new Builder( branch );
    }

    public static final class Builder
    {
        private final Branch branch;

        private Long numberOfNodes = 0L;

        private Long numberOfVersions = 0L;

        private final Long startTime;

        private Long endTime;

        private Duration duration;

        private final List<LoadError> errors = Lists.newArrayList();

        private Builder( final Branch branch )
        {
            this.branch = branch;
            this.startTime = System.currentTimeMillis();
        }

        public Builder addedNode()
        {
            this.numberOfNodes += 1;
            return this;
        }

        public Builder addedNodes( final Long val )
        {
            numberOfNodes += val;
            return this;
        }

        public Builder addedVersions( final Long val )
        {
            numberOfVersions += val;
            return this;
        }

        public Builder duration( final Duration duration )
        {
            this.duration = duration;
            return this;
        }

        public Builder error( final LoadError error )
        {
            this.errors.add( error );
            return this;
        }

        public BranchLoadResult build()
        {
            this.endTime = System.currentTimeMillis();
            return new BranchLoadResult( this );
        }
    }
}
