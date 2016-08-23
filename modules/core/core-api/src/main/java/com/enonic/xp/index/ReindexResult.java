package com.enonic.xp.index;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.Sets;

import com.enonic.xp.branch.BranchIds;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.repository.RepositoryId;

@Beta
public class ReindexResult
{
    private final Duration duration;

    private final Instant startTime;

    private final Instant endTime;

    private final NodeIds reindexNodes;

    private final BranchIds branchIds;

    private final RepositoryId repositoryId;

    private ReindexResult( Builder builder )
    {
        this.duration = builder.duration;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.reindexNodes = NodeIds.from( builder.nodeIds );
        this.repositoryId = builder.repositoryId;
        this.branchIds = builder.branchIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Duration getDuration()
    {
        return duration;
    }

    public Instant getStartTime()
    {
        return startTime;
    }

    public Instant getEndTime()
    {
        return endTime;
    }

    public NodeIds getReindexNodes()
    {
        return reindexNodes;
    }

    public BranchIds getBranchIds()
    {
        return branchIds;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public static final class Builder
    {
        private final Set<NodeId> nodeIds = Sets.newHashSet();

        private Duration duration;

        private Instant startTime;

        private Instant endTime;

        private BranchIds branchIds;

        private RepositoryId repositoryId;

        private Builder()
        {
        }

        public Builder duration( Duration duration )
        {
            this.duration = duration;
            return this;
        }

        public Builder startTime( Instant startTime )
        {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime( Instant endTime )
        {
            this.endTime = endTime;
            return this;
        }

        public Builder add( final NodeId nodeId )
        {
            this.nodeIds.add( nodeId );
            return this;
        }

        public Builder branches( final BranchIds branchIds )
        {
            this.branchIds = branchIds;
            return this;
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public ReindexResult build()
        {
            return new ReindexResult( this );
        }
    }
}
