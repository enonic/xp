package com.enonic.xp.index;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.repository.RepositoryId;

@PublicApi
public class ReindexResult
{
    private final Duration duration;

    private final Instant startTime;

    private final Instant endTime;

    private final NodeIds reindexNodes;

    private final Branches branches;

    private final RepositoryId repositoryId;

    private ReindexResult( Builder builder )
    {
        this.duration = builder.duration;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.reindexNodes = NodeIds.from( builder.nodeIds );
        this.repositoryId = builder.repositoryId;
        this.branches = builder.branches;
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

    public Branches getBranches()
    {
        return branches;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public static final class Builder
    {
        private final Set<NodeId> nodeIds = new HashSet<>();

        private Duration duration;

        private Instant startTime;

        private Instant endTime;

        private Branches branches;

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

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
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
