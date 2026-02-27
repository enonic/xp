package com.enonic.xp.repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

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

    private static final NameValidator REPOSITORY_ID_VALIDATOR =
        NameValidator.builder( RepositoryId.class ).maxLength( 63 ).regex( Pattern.compile( "^[a-z0-9\\-][a-z0-9_\\-.]*$" ) ).build();

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
        return new RepositoryId( REPOSITORY_ID_VALIDATOR.validate( value ) );
    }
}
