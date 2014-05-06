package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;

public abstract class AbstractDescriptorBasedPageComponent<DESCRIPTOR_KEY extends DescriptorKey>
    extends AbstractPageComponent
{
    private final DESCRIPTOR_KEY descriptor;

    private final RootDataSet config;

    protected AbstractDescriptorBasedPageComponent( final Properties<DESCRIPTOR_KEY> properties )
    {
        super( properties );
        this.descriptor = properties.descrpitor;
        this.config = properties.config;
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
        extends AbstractPageComponent.Properties
    {
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
