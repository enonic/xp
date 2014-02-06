package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.rendering.Component;

public abstract class PageComponent<DESCRIPTOR_KEY extends DescriptorKey>
    implements Component
{
    private ComponentName name;

    private final DESCRIPTOR_KEY descriptor;

    private final RootDataSet config;

    private ComponentPath path;

    protected PageComponent( final Properties<DESCRIPTOR_KEY> properties )
    {
        Preconditions.checkNotNull( properties.name, "name cannot be null" );
        this.descriptor = properties.descrpitor;
        this.name = properties.name;
        this.config = properties.config;
    }

    public ComponentName getName()
    {
        return name;
    }

    public void setPath( final ComponentPath path )
    {
        this.path = path;
    }

    public ComponentPath getPath()
    {
        return path;
    }

    public DESCRIPTOR_KEY getDescriptor()
    {
        return descriptor;
    }

    public boolean hasConfig()
    {
        return config != null;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public static class Properties<DESCRIPTOR_KEY extends DescriptorKey>
    {
        protected ComponentName name;

        protected DESCRIPTOR_KEY descrpitor;

        protected RootDataSet config;
    }

    public static class Builder<DESCRIPTOR_KEY extends DescriptorKey>
        extends Properties<DESCRIPTOR_KEY>
    {
        protected Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder<DESCRIPTOR_KEY> descriptor( DESCRIPTOR_KEY value )
        {
            this.descrpitor = value;
            return this;
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }
    }
}
