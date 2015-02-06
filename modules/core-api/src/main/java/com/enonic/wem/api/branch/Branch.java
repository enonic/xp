package com.enonic.wem.api.branch;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public final class Branch
{
    private final String name;

    private Branch( final Builder builder )
    {
        this.name = builder.name;
    }

    public static Branch from( final String name )
    {
        return Branch.create().
            name( name ).
            build();
    }

    public String getName()
    {
        return name;
    }


    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        return name;
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
        return name.equals( branch.name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    public static final class Builder
    {
        private String name;

        private Builder()
        {
        }

        public Builder name( String name )
        {
            this.name = name;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( !Strings.isNullOrEmpty( this.name ) );
        }

        public Branch build()
        {
            return new Branch( this );
        }
    }
}


