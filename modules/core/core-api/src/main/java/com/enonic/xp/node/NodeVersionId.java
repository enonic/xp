package com.enonic.xp.node;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeVersionId
    extends UUID
{
    public NodeVersionId()
    {
        super();
    }

    private NodeVersionId( final String value )
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

        final NodeVersionId other = (NodeVersionId) o;
        return Objects.equals( value, other.value );
    }

    public static NodeVersionId from( String string )
    {
        return new NodeVersionId( string );
    }

    public static NodeVersionId from( Object object )
    {
        Preconditions.checkNotNull( object, "object cannot be null" );
        return new NodeVersionId( object.toString() );
    }
}
