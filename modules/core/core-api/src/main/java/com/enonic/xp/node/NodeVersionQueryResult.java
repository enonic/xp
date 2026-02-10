package com.enonic.xp.node;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
@NullMarked
public final class NodeVersionQueryResult
{
    private final NodeVersions nodeVersions;

    private final long totalHits;

    private NodeVersionQueryResult( Builder builder )
    {
        nodeVersions = Objects.requireNonNull( builder.nodeVersions );
        totalHits = builder.totalHits;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersions getNodeVersions()
    {
        return nodeVersions;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public static final class Builder
    {
        private @Nullable NodeVersions nodeVersions;

        private long totalHits;

        private Builder()
        {
        }

        public Builder entityVersions( final NodeVersions nodeVersions )
        {
            this.nodeVersions = nodeVersions;
            return this;
        }

        public Builder totalHits( final long totalHits )
        {
            this.totalHits = totalHits;
            return this;
        }

        public NodeVersionQueryResult build()
        {
            return new NodeVersionQueryResult( this );
        }
    }
}
