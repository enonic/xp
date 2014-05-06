package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.rendering.Component;

public abstract class AbstractPageComponent
    implements Component, PageComponent
{
    private ComponentName name;

    private ComponentPath path;

    protected AbstractPageComponent( final Properties properties )
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
        return path;
    }

    public void setPath( final ComponentPath path )
    {
        this.path = path;
    }

    public static class Properties
    {
        protected ComponentName name;
    }

    public static class Builder
        extends Properties
    {
        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }
    }
}
