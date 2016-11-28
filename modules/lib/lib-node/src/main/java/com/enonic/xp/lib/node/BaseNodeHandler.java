package com.enonic.xp.lib.node;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;

public abstract class BaseNodeHandler
{
    protected final NodeService nodeService;

    protected BaseNodeHandler( final Builder builder )
    {
        nodeService = builder.nodeService;
    }

    protected Node doGetNode( final NodeKey nodeKey )
    {
        if ( !nodeKey.isId() )
        {
            return nodeService.getByPath( nodeKey.getAsPath() );
        }
        else
        {
            return nodeService.getById( nodeKey.getAsNodeId() );
        }
    }

    protected <T> T valueOrDefault( final T value, final T defValue )
    {
        return value == null ? defValue : value;
    }

    public abstract Object execute();

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
