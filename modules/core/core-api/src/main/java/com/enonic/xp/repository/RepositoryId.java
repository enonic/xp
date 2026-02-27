package com.enonic.xp.repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.core.internal.NameValidator;

@PublicApi
@NullMarked
public final class RepositoryId
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 0;

    private final String value;

    private RepositoryId( final String value )
    {
        this.value = Objects.requireNonNull( value );
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
        return new RepositoryId( NameValidator.requireValidRepositoryId( value ) );
    }
}
