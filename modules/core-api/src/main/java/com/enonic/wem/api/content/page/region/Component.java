package com.enonic.wem.api.content.page.region;


import java.util.Objects;

public abstract class Component
    implements com.enonic.wem.api.rendering.Component
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
        return ComponentPath.from( region.getRegionPath(), region.getIndex( this ) );
    }

    public void setRegion( final Region region )
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
