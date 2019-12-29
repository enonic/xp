package com.enonic.xp.node;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeCommitId
    extends UUID
{
    public NodeCommitId()
    {
        super();
    }

    private NodeCommitId( final String value )
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

        final NodeCommitId other = (NodeCommitId) o;
        return Objects.equals( value, other.value );
    }

    public static NodeCommitId from( String string )
    {
        return new NodeCommitId( string );
    }

    public static NodeCommitId from( Object object )
    {
        Preconditions.checkNotNull( object, "object cannot be null" );
        return new NodeCommitId( object.toString() );
    }
}
