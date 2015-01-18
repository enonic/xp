package com.enonic.wem.api.content.page.region;

import java.util.Objects;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.data.PropertyTree;

public abstract class DescriptorBasedComponent
    extends Component
{
    private final DescriptorKey descriptor;

    private final PropertyTree config;

    protected DescriptorBasedComponent( final Builder builder )
    {
        super( builder );
        this.descriptor = builder.descrpitor;
        this.config = builder.config != null ? builder.config : new PropertyTree();
    }

    public DescriptorKey getDescriptor()
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
        if ( !( o instanceof DescriptorBasedComponent ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final DescriptorBasedComponent that = (DescriptorBasedComponent) o;

        return Objects.equals( descriptor, that.descriptor ) && Objects.equals( config, that.config );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), config, descriptor );
    }

    public static class Builder
        extends Component.Builder
    {
        protected DescriptorKey descrpitor;

        protected PropertyTree config;

        protected Builder()
        {
            // Default
        }

        protected Builder( final DescriptorBasedComponent source )
        {
            super( source );
            this.config = source.getConfig() != null ? source.getConfig().copy() : null;
            this.descrpitor = source.getDescriptor();
        }

        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder descriptor( DescriptorKey value )
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
