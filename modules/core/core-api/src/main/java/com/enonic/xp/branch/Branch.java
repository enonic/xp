package com.enonic.xp.branch;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.core.internal.NameValidator;

@PublicApi
public final class Branch
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 0;

    public static final int BRANCH_MAX_LENGTH = 63;

    private static final NameValidator BRANCH_VALIDATOR =
        NameValidator.builder( Branch.class ).maxLength( BRANCH_MAX_LENGTH ).regex( Pattern.compile( "^[a-z0-9][a-z0-9.-]*$" ) ).build();

    private static final Branch MASTER = new Branch( "master" );

    private static final Branch DRAFT = new Branch( "draft" );

    private final String value;

    private Branch( final String value )
    {
        this.value = Objects.requireNonNull( value );
    }

    public static Branch from( final String name )
    {
        return switch ( Objects.requireNonNull( name, "Branch cannot be null" ) )
        {
            case "master" -> MASTER;
            case "draft" -> DRAFT;
            default -> new Branch( BRANCH_VALIDATOR.validate( name ) );
        };
    }

    public String getValue()
    {
        return value;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof Branch && value.equals( ( (Branch) o ).value );
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    public static final class Builder
    {
        private String value;

        private Builder()
        {
        }

        public Builder value( String value )
        {
            this.value = value;
            return this;
        }

        public Branch build()
        {
            return Branch.from( this.value );
        }
    }
}


