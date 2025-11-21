package com.enonic.xp.node;

import java.util.Objects;

import com.enonic.xp.branch.Branch;

public final class PushNodeParams
{
    private final NodeIds ids;

    private final Branch target;

    private final PushNodesListener pushListener;

    private final NodeDataProcessor processor;

    private PushNodeParams( final Builder builder )
    {
        this.ids = Objects.requireNonNull( builder.ids, "ids is required" );
        this.target = Objects.requireNonNull( builder.target, "target is required" );
        this.pushListener = builder.pushListener;
        this.processor = builder.processor;
    }

    public NodeIds getIds()
    {
        return ids;
    }

    public Branch getTarget()
    {
        return target;
    }

    public PushNodesListener getPushListener()
    {
        return pushListener;
    }

    public NodeDataProcessor getProcessor()
    {
        return processor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private NodeIds ids;

        private Branch target;

        private PushNodesListener pushListener;

        private NodeDataProcessor processor;

        private Builder()
        {
        }

        public Builder ids( final NodeIds ids )
        {
            this.ids = ids;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder publishListener( final PushNodesListener pushListener )
        {
            this.pushListener = pushListener;
            return this;
        }

        public Builder processor( final NodeDataProcessor processor )
        {
            this.processor = processor;
            return this;
        }

        public PushNodeParams build()
        {
            return new PushNodeParams( this );
        }
    }
}
