package com.enonic.xp.repository;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

@Beta
public final class RepositoryId
{
    private static final String VALID_REPOSITORY_ID_REGEX = "([a-z0-9\\-:])([a-z0-9_\\-\\.:])*";

    private final String value;

    private RepositoryId( final String value )
    {
        final String sanitizedValue = Strings.isNullOrEmpty( value ) ? value : value.toLowerCase();

        Preconditions.checkNotNull( sanitizedValue, "RepositoryId cannot be null" );
        Preconditions.checkArgument( !sanitizedValue.trim().isEmpty(), "RepositoryId cannot be blank" );
        Preconditions.checkArgument( sanitizedValue.matches( "^" + VALID_REPOSITORY_ID_REGEX + "$" ),
                                     "RepositoryId format incorrect: " + value );
        this.value = value;
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
