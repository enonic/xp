package com.enonic.xp.lib.node;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;

public abstract class AbstractNodeHandler
{
    protected final NodeService nodeService;

    protected AbstractNodeHandler( final Builder builder )
    {
        nodeService = builder.nodeService;
    }

    protected Node doGetNode( final NodeKey nodeKey )
    {
        if ( nodeKey == null )
        {
            return null;
        }

        if ( !nodeKey.isId() )
        {
            return nodeService.getByPath( nodeKey.getAsPath() );
        }
        else
        {
            return nodeService.getById( nodeKey.getAsNodeId() );
        }
    }

    protected NodeId getNodeId( final NodeKey nodeKey )
    {
        return doGetNodeId( nodeKey );
    }

    protected NodeIds getNodeIds( final NodeKeys nodeKeys )
    {
        if ( nodeKeys == null )
        {
            return NodeIds.empty();
        }

        final NodeIds.Builder builder = NodeIds.create();

        for ( final NodeKey nodeKey : nodeKeys )
        {
            final NodeId nodeId = doGetNodeId( nodeKey );
            if ( nodeId != null )
            {
                builder.add( nodeId );
            }
        }

        return builder.build();
    }

    private NodeId doGetNodeId( final NodeKey nodeKey )
    {
        if ( nodeKey.isId() )
        {
            return nodeKey.getAsNodeId();
        }

        final Node byPath = nodeService.getByPath( nodeKey.getAsPath() );

        if ( byPath != null )
        {
            return byPath.id();
        }

        return null;
    }

    protected <T> T valueOrDefault( final T value, final T defValue )
    {
        return value == null ? defValue : value;
    }

    public abstract Object execute();

    public abstract static class Builder<B extends Builder>
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
