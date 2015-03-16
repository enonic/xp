package com.enonic.xp.node;


import java.util.Objects;
import java.util.UUID;

import com.google.common.base.Preconditions;

public class NodeId
{
    private final String value;

    private static final String VALID_NODE_ID_PATTERN = "([a-z0-9A-Z_\\-\\.:])*";

    public NodeId()
    {
        this.value = UUID.randomUUID().toString();
    }

    private NodeId( final String value )
    {
        Preconditions.checkNotNull( value, "NodeId cannot be null" );
        Preconditions.checkArgument( !value.trim().isEmpty(), "NodeId cannot be blank" );
        Preconditions.checkArgument( value.matches( "^" + VALID_NODE_ID_PATTERN + "$" ), "NodeId format incorrect: " + value );

        this.value = value;
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

    @Override
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
