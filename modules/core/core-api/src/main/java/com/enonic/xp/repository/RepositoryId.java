package com.enonic.xp.repository;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.util.StringChecker;

@Beta
public final class RepositoryId
{
    private final String value;

    private RepositoryId( final String value )
    {
        Preconditions.checkNotNull( value );
        this.value = StringChecker.defaultCheck( value, "Not a valid value for RepositoryId [" + value + "]" );
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof RepositoryId ) && Objects.equals( this.value, ( (RepositoryId) o ).value );
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

    public static RepositoryId from( final String value )
    {
        return new RepositoryId( value );
    }
}
