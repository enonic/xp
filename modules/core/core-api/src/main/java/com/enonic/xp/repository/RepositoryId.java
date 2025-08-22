package com.enonic.xp.repository;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class RepositoryId
    implements Serializable
{
    private static final long serialVersionUID = 0;

    public static final Pattern VALID_REPOSITORY_ID_REGEX = Pattern.compile( "([a-z0-9\\-:])([a-z0-9_\\-\\.:])*" );

    private final String value;

    private RepositoryId( final String value )
    {
        Objects.requireNonNull( value, "RepositoryId cannot be null" );
        Preconditions.checkArgument( !value.isBlank(), "RepositoryId cannot be blank" );
        Preconditions.checkArgument( VALID_REPOSITORY_ID_REGEX.matcher( value ).matches(), "RepositoryId format incorrect: %s", value );
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
