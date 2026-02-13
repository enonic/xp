package com.enonic.xp.node;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branches;

@PublicApi
@NullMarked
public final class GetActiveNodeVersionsParams
{
    private final NodeId nodeId;

    private final Branches branches;

    private GetActiveNodeVersionsParams( Builder builder )
    {
        nodeId = Objects.requireNonNull( builder.nodeId, "nodeId is required" );
        branches = Objects.requireNonNull( builder.branches, "branches is required" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static final class Builder
    {
        @Nullable
        private NodeId nodeId;

        @Nullable
        private Branches branches;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }

        public GetActiveNodeVersionsParams build()
        {
            return new GetActiveNodeVersionsParams( this );
        }
    }
}
