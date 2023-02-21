package com.enonic.xp.cluster;

import java.io.Serializable;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ClusterNodeId
    implements Serializable
{
    private static final long serialVersionUID = 0;

    private final String value;

    private ClusterNodeId( final String value )
    {
        this.value = Objects.requireNonNull( value );
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof ClusterNodeId && this.value.equals( ( (ClusterNodeId) o ).value );
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    public static ClusterNodeId from( final String name )
    {
        return new ClusterNodeId( name );
    }
}
