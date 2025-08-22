package com.enonic.xp.impl.scheduler;

import java.util.Objects;

import com.enonic.xp.node.NodeService;

public abstract class AbstractSchedulerCommand
{
    protected final NodeService nodeService;

    protected <B extends Builder<B>> AbstractSchedulerCommand( final Builder<B> builder )
    {
        this.nodeService = builder.nodeService;
    }

    public abstract static class Builder<B extends Builder<B>>
    {
        private NodeService nodeService;

        protected Builder()
        {
        }

        public B nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return (B) this;
        }

        protected void validate()
        {
            Objects.requireNonNull( nodeService );
        }

        abstract AbstractSchedulerCommand build();
    }
}
