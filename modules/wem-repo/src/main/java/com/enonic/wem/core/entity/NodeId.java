package com.enonic.wem.core.entity;


import java.util.Objects;
import java.util.UUID;

import com.google.common.base.Preconditions;

public class NodeId
{
    private final String value;

    public NodeId()
    {
        this.value = UUID.randomUUID().toString();
    }

    private NodeId( final String string )
    {
        Preconditions.checkNotNull( string, "string cannot be null" );
        this.value = string;
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

        final NodeId other = (NodeId) o;
        return Objects.equals( value, other.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( value );
    }

    public String toString()
    {
        return value;
    }

    public static NodeId from( String string )
    {
        return new NodeId( string );
    }

    public static NodeId from( Object object )
    {
        Preconditions.checkNotNull( object, "object cannot be null" );
        return new NodeId( object.toString() );
    }
}
