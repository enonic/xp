package com.enonic.wem.api.content.page.region;


import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.data.PropertyTree;

public final class PartComponent
    extends DescriptorBasedComponent
{
    public PartComponent( final Builder builder )
    {
        super( builder );
    }

    public static Builder newPartComponent()
    {
        return new Builder();
    }

    public static Builder newPartComponent( final PartComponent source )
    {
        return new Builder( source );
    }

    public Component copy()
    {
        return newPartComponent( this ).build();
    }

    @Override
    public ComponentType getType()
    {
        return PartComponentType.INSTANCE;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PartComponent ) )
        {
            return false;
        }

        return super.equals( o );
    }

    public static class Builder
        extends DescriptorBasedComponent.Builder
    {
        private Builder()
        {
            // Default
        }

        private Builder( final PartComponent source )
        {
            super( source );
        }

        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = new ComponentName( value );
            return this;
        }

        public Builder descriptor( DescriptorKey value )
        {
            this.descrpitor = value;
            return this;
        }

        public Builder descriptor( String value )
        {
            this.descrpitor = DescriptorKey.from( value );
            return this;
        }

        public Builder config( final PropertyTree config )
        {
            this.config = config;
            return this;
        }

        public PartComponent build()
        {
            return new PartComponent( this );
        }
    }
}
