package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.Component;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.ComponentType;
import com.enonic.wem.api.content.page.DescriptorBasedComponent;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.PropertyTree;

public final class PartComponent
    extends DescriptorBasedComponent<PartDescriptorKey>
    implements RegionPlaceableComponent
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
        extends DescriptorBasedComponent.Builder<PartDescriptorKey>
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

        public Builder descriptor( PartDescriptorKey value )
        {
            this.descrpitor = value;
            return this;
        }

        public Builder descriptor( String value )
        {
            this.descrpitor = PartDescriptorKey.from( value );
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
