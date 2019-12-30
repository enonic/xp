package com.enonic.xp.region;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class RegionDescriptor
{
    private final String name;

    private RegionDescriptor( final Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name cannot be null" );
        this.name = builder.name;
    }

    public String getName()
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

        final RegionDescriptor that = (RegionDescriptor) o;

        return Objects.equals( this.name, that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name );
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String name;

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public RegionDescriptor build()
        {
            return new RegionDescriptor( this );
        }
    }
}
