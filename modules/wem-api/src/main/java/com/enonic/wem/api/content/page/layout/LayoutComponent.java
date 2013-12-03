package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.content.page.BasePageComponent;
import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public final class LayoutComponent
    extends BasePageComponent<LayoutTemplateKey>
    implements RegionPlaceableComponent
{
    private final RootDataSet config;

    public LayoutComponent( final Builder builder )
    {
        super( builder.layoutTemplateKey );
        this.config = builder.config;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public static Builder newLayout()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RootDataSet config;

        private LayoutTemplateKey layoutTemplateKey;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder template( final LayoutTemplateKey layoutTemplateKey )
        {
            this.layoutTemplateKey = layoutTemplateKey;
            return this;
        }

        public LayoutComponent build()
        {
            return new LayoutComponent( this );
        }
    }
}
