package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.AbstractDescriptorBasedPageComponent;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponentType;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.PropertyTree;

public final class PartComponent
    extends AbstractDescriptorBasedPageComponent<PartDescriptorKey>
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

    @Override
    public PageComponentType getType()
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
        extends AbstractDescriptorBasedPageComponent.Builder<PartDescriptorKey>
    {
        private Builder()
        {
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
