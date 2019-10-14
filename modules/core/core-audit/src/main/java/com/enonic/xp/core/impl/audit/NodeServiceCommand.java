package com.enonic.xp.core.impl.audit;

import com.enonic.xp.node.NodeService;

public abstract class NodeServiceCommand<R>
{
    protected final NodeService nodeService;

    protected NodeServiceCommand( final Builder builder )
    {
        this.nodeService = builder.nodeService;
    }

    abstract R execute();

    public static class Builder<B extends Builder>
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
    }
}
