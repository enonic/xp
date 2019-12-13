package com.enonic.xp.region;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

@Beta
public abstract class Component
{
    private Region region = null;

    protected Component( final Builder properties )
    {
    }

    public abstract ComponentType getType();

    public abstract ComponentName getName();

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

        return Objects.equals( this.getName(), that.getName() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.getName() );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "type", getType() ).
            add( "path", getPath() ).
            toString();
    }

    public static class Builder<T extends Builder<T>>
    {
        protected ComponentName name;

        protected Builder()
        {
            // Default
        }

        protected Builder( Component source )
        {
        }

    }
}
