package com.enonic.xp.branch;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.util.CharacterChecker;

@Beta
public final class BranchId
{
    private final String value;

    private BranchId( final Builder builder )
    {
        this.value = CharacterChecker.check( builder.value, "Not a valid value for BranchId [" + builder.value + "]" );
    }

    public static BranchId from( final String name )
    {
        return BranchId.create().
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

        final BranchId branchId = (BranchId) o;
        return value.equals( branchId.value );
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

        public BranchId build()
        {
            return new BranchId( this );
        }
    }
}


