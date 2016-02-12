package com.enonic.xp.region;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

@Beta
public abstract class Component
{
    private ComponentName name;

    private Region region = null;

    protected Component( final Builder properties )
    {
        this.name = properties.name;
    }

    public abstract ComponentType getType();

    public ComponentName getName()
    {
        return name;
    }

    public ComponentPath getPath()
    {
        return region == null ? null : ComponentPath.from( region.getRegionPath(), region.getIndex( this ) );
    }

    void setRegion( final Region region )
    {
        this.region = region;
    }

    public abstract Component copy();

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

        final Component that = (Component) o;

        return Objects.equals( name, that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "type", getType() ).
            add( "name", name ).
            add( "path", getPath() ).
            toString();
    }

    public static class Builder
    {
        protected ComponentName name;

        protected Builder()
        {
            // Default
        }

        protected Builder( Component source )
        {
            this.name = source.name;
        }

        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }
    }
}
