package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.content.page.AbstractDescriptorBasedPageComponent;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentType;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public final class LayoutComponent
    extends AbstractDescriptorBasedPageComponent<LayoutDescriptorKey>
    implements RegionPlaceableComponent
{
    private LayoutRegions regions;

    public LayoutComponent( final Builder builder )
    {
        super( builder );
        if ( builder.regions == null )
        {
            this.regions = LayoutRegions.newLayoutRegions().build();
        }
        else
        {
            this.regions = builder.regions;
        }

        for ( final Region region : this.regions )
        {
            region.setParent( this );
        }
    }

    public static Builder newLayoutComponent()
    {
        return new Builder();
    }

    @Override
    public PageComponentType getType()
    {
        return LayoutComponentType.INSTANCE;
    }

    public boolean hasRegions()
    {
        return regions != null;
    }

    public LayoutRegions getRegions()
    {
        return regions;
    }

    public PageComponent getComponent( final ComponentPath path )
    {
        return regions.getComponent( path );
    }

    public static class Builder
        extends AbstractDescriptorBasedPageComponent.Builder<LayoutDescriptorKey>
    {
        private LayoutRegions regions;

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

        public Builder descriptor( LayoutDescriptorKey value )
        {
            this.descrpitor = value;
            return this;
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder regions( final LayoutRegions value )
        {
            this.regions = value;
            return this;
        }

        public LayoutComponent build()
        {
            return new LayoutComponent( this );
        }
    }
}
