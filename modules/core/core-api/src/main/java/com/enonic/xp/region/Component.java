package com.enonic.xp.region;

import java.util.Objects;

import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class Component
{
    private Region region = null;

    protected Component( final Builder properties )
    {
    }

    public abstract ComponentType getType();

    public ComponentPath getPath()
    {
        return region == null ? ComponentPath.from( "/" ) : ComponentPath.from( region.getRegionPath(), region.getIndex( this ) );
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

        return Objects.equals( this.getType().toString(), that.getType().toString() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.getType().toString() );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "type", getType() ).
            add( "path", getPath() ).
            toString();
    }

    public static class Builder
    {
        protected Builder()
        {
            // Default
        }

        protected Builder( Component source )
        {
        }
    }
}
