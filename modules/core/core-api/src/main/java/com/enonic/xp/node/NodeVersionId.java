package com.enonic.xp.node;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public final class NodeVersionId
{
    private final String value;

    private NodeVersionId( final String value )
    {
        Preconditions.checkNotNull( value );
        this.value = value;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof NodeVersionId ) && Objects.equals( this.value, ( (NodeVersionId) o ).value );
    }

    @Override
    public int hashCode()
    {
        return this.value.hashCode();
    }

    @Override
    public String toString()
    {
        return this.value;
    }

    public static NodeVersionId from( final String value )
    {
        return new NodeVersionId( value );
    }
}
