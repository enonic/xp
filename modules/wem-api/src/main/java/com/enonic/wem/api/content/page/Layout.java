package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.page.region.RegionPlaceableComponent;
import com.enonic.wem.api.data.RootDataSet;

public final class Layout
    extends PageComponent<LayoutTemplateId>
    implements RegionPlaceableComponent
{
    private final RootDataSet config;

    public Layout( final Builder builder )
    {
        super( builder.layoutTemplateId );
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

        private LayoutTemplateId layoutTemplateId;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder layoutTemplateId( final LayoutTemplateId layoutTemplateId )
        {
            this.layoutTemplateId = layoutTemplateId;
            return this;
        }

        public Layout build()
        {
            return new Layout( this );
        }
    }
}
