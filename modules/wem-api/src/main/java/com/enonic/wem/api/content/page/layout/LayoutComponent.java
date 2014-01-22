package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public final class LayoutComponent
    extends PageComponent<LayoutTemplateKey>
    implements RegionPlaceableComponent
{
    private LayoutRegions regions;

    public LayoutComponent( final Builder builder )
    {
        super( builder );
        this.regions = builder.regions;
    }

    public boolean hasRegions()
    {
        return regions != null;
    }

    public LayoutRegions getRegions()
    {
        return regions;
    }

    public static Builder newLayoutComponent()
    {
        return new Builder();
    }

    public PageComponent getComponent( final ComponentName name )
    {
        return regions.getComponent( name );
    }

    public static class Builder
        extends PageComponent.Builder<LayoutTemplateKey>
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

        public Builder template( LayoutTemplateKey value )
        {
            this.template = value;
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
