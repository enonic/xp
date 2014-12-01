package com.enonic.wem.api.content.page;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data2.PropertyTree;

public abstract class AbstractDescriptorBasedPageComponent<DESCRIPTOR_KEY extends DescriptorKey>
    extends AbstractPageComponent
    implements DescriptorBasedPageComponent
{
    private final DESCRIPTOR_KEY descriptor;

    private final PropertyTree config;

    protected AbstractDescriptorBasedPageComponent( final Builder<DESCRIPTOR_KEY> builder )
    {
        super( builder );
        this.descriptor = builder.descrpitor;
        Preconditions.checkNotNull( builder.config, "config cannot be null" );
        this.config = builder.config;
    }

    public DESCRIPTOR_KEY getDescriptor()
    {
        return descriptor;
    }

    public boolean hasConfig()
    {
        return config != null;
    }

    public PropertyTree getConfig()
    {
        return config;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof AbstractDescriptorBasedPageComponent ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final AbstractDescriptorBasedPageComponent that = (AbstractDescriptorBasedPageComponent) o;

        return Objects.equals( descriptor, that.descriptor ) && Objects.equals( config, that.config );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), config, descriptor );
    }

    public static class Builder<DESCRIPTOR_KEY extends DescriptorKey>
        extends AbstractPageComponent.Builder
    {
        protected DESCRIPTOR_KEY descrpitor;

        protected PropertyTree config;

        protected Builder()
        {
            this.config = new PropertyTree();
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

        public Builder config( final PropertyTree config )
        {
            this.config = config;
            return this;
        }
    }
}
