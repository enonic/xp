package com.enonic.xp.branch;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class Branch
{
    private static final String VALID_REPOSITORY_ID_REGEX = "([a-zA-Z0-9\\-:])([a-zA-Z0-9_\\-\\.:])*";

    private final String value;

    private Branch( final Builder builder )
    {
        Preconditions.checkArgument( !isNullOrEmpty( builder.value ), "Branch name cannot be null or empty" );
        Preconditions.checkArgument( builder.value.matches( "^" + VALID_REPOSITORY_ID_REGEX + "$" ),
                                     "Branch name format incorrect: " + builder.value );
        this.value = builder.value;
    }

    public static Branch from( final String name )
    {
        return Branch.create().
            value( name ).
            build();
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
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Branch branch = (Branch) o;
        return value.equals( branch.value );
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
            return new Branch( this );
        }
    }
}


