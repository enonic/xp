package com.enonic.wem.api.content.page;

import com.enonic.wem.api.data.RootDataSet;

public final class Layout
    extends PageComponent<LayoutTemplateId>
{
    private final RootDataSet liveEditConfig;

    /**
     * Values will override any values in LayoutTemplate.pageConfig.
     */
    private final RootDataSet layoutConfig;

    public Layout( final Builder builder )
    {
        super( builder.layoutTemplateId );
        this.liveEditConfig = builder.liveEditConfig;
        this.layoutConfig = builder.layoutConfig;
    }

    public RootDataSet getLiveEditConfig()
    {
        return liveEditConfig;
    }

    public RootDataSet getLayoutConfig()
    {
        return layoutConfig;
    }

    public static Builder newLayout()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RootDataSet layoutConfig;

        private RootDataSet liveEditConfig;

        private LayoutTemplateId layoutTemplateId;

        private Builder()
        {
            this.layoutConfig = RootDataSet.newDataSet().build().toRootDataSet();
            this.liveEditConfig = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder layoutConfig( final RootDataSet config )
        {
            this.layoutConfig = config;
            return this;
        }

        public Builder liveEditConfig( final RootDataSet config )
        {
            this.liveEditConfig = config;
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
