package com.enonic.xp.archive;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ArchivedContainerId
{
    private final String id;

    private ArchivedContainerId( final String id )
    {
        this.id = id;
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
        final ArchivedContainerId that = (ArchivedContainerId) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id );
    }

    @Override
    public String toString()
    {
        return this.id;
    }

    public static ArchivedContainerId from( final String id )
    {
        Preconditions.checkNotNull( id, "ArchivedContainerId cannot be null" );
        Preconditions.checkArgument( !id.isBlank(), "ArchivedContainerId cannot be blank" );
        return new ArchivedContainerId( id );
    }
}
