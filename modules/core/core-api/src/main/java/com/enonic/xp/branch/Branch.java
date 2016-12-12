package com.enonic.xp.branch;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.util.CharacterChecker;

@Beta
public final class Branch
{
    private final String value;

    private Branch( final Builder builder )
    {
        this.value = CharacterChecker.check( builder.value, "Not a valid value for BranchId [" + builder.value + "]" );
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

        private void validate()
        {
            Preconditions.checkArgument( !Strings.isNullOrEmpty( this.value ) );
        }

        public Branch build()
        {
            return new Branch( this );
        }
    }
}


