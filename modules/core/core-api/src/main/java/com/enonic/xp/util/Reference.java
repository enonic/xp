package com.enonic.xp.util;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.NodeId;

@PublicApi
public final class Reference
{
    private final NodeId nodeId;

    public Reference( final NodeId nodeId )
    {
        this.nodeId = Objects.requireNonNull( nodeId );
    }

    public static Reference from( final String value )
    {
        return new Reference( NodeId.from( value ) );
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    @Override
    public String toString()
    {
        return nodeId.toString();
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof Reference && nodeId.equals( ( (Reference) o ).nodeId );
    }

    @Override
    public int hashCode()
    {
        return nodeId.hashCode();
    }
}
