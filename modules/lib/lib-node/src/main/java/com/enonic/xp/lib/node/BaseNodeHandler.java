package com.enonic.xp.lib.node;

import com.enonic.xp.node.NodeService;

public abstract class BaseNodeHandler
{
    protected final NodeService nodeService;

    protected BaseNodeHandler( final Builder builder )
    {
        nodeService = builder.nodeService;
    }

    public static abstract class Builder<B extends Builder>
    {
        private NodeService nodeService;

        Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B nodeService( final NodeService val )
        {
            nodeService = val;
            return (B) this;
        }

    }
}
