package com.enonic.xp.branch;

import java.io.Serializable;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class Branch
    implements Serializable
{
    private static final long serialVersionUID = 0;

    private static final Branch MASTER = new Branch( "master" );

    private static final Branch DRAFT = new Branch( "draft" );

    private static final String VALID_BRANCH_ID_REGEX = "^([a-zA-Z0-9\\-:])([a-zA-Z0-9\\-.:])*$";

    private final String value;

    private Branch( final String value )
    {
        this.value = Objects.requireNonNull( value );
    }

    public static Branch from( final String name )
    {
        switch ( name )
        {
            case "master":
                return MASTER;
            case "draft":
                return DRAFT;
            default:
            {
                Preconditions.checkArgument( !isNullOrEmpty( name ), "Branch name cannot be null or empty" );
                Preconditions.checkArgument( name.matches( VALID_BRANCH_ID_REGEX ), "Branch name format incorrect: %s", name );
                return new Branch( name );
            }
        }
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


