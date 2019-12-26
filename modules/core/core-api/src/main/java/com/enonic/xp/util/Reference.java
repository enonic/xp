package com.enonic.xp.util;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.NodeId;

@PublicApi
public class Reference
{
    private final NodeId nodeId;

    public Reference( final NodeId nodeId )
    {
        this.nodeId = nodeId;
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
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Reference reference = (Reference) o;

        return nodeId != null ? nodeId.equals( reference.nodeId ) : reference.nodeId == null;
    }

    @Override
    public int hashCode()
    {
        return nodeId != null ? nodeId.hashCode() : 0;
    }
}
