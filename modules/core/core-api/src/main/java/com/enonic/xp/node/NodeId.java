package com.enonic.xp.node;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeId
    extends com.enonic.xp.node.UUID
{
    public NodeId()
    {
        super();
    }

    private NodeId( final String value )
    {
        super( value );
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
