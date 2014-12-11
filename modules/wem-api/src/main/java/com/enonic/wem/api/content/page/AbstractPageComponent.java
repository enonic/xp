package com.enonic.wem.api.content.page;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.rendering.Component;

public abstract class AbstractPageComponent
    implements Component, PageComponent
{
    private ComponentName name;

    private Region parent = null;

    protected AbstractPageComponent( final Builder properties )
    {
        Preconditions.checkNotNull( properties.name, "name cannot be null" );
        this.name = properties.name;
    }

    public abstract PageComponentType getType();


    public ComponentName getName()
    {
        return name;
    }

    public ComponentPath getPath()
    {
        return ComponentPath.from( parent.getRegionPath(), parent.getIndex( this ) );
    }

    @Override
    public void setParent( final Region region )
    {
        this.parent = region;
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

        final AbstractPageComponent that = (AbstractPageComponent) o;

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

        protected Builder( AbstractPageComponent source )
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
