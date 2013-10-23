package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;

public final class Page
    extends PageComponent<PageTemplateId>
{
    /**
     * Values will override any values in PageTemplate.pageConfig.
     */
    private final RootDataSet config;

    private final RootDataSet liveEditConfig;

    private Page( final Builder builder )
    {
        super( builder.pageTemplateId );
        this.config = builder.config;
        this.liveEditConfig = builder.liveEditConfig;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public RootDataSet getLiveEditConfig()
    {
        return liveEditConfig;
    }

    public static Builder newPage()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RootDataSet config;

        private RootDataSet liveEditConfig;

        private PageTemplateId pageTemplateId;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
            this.liveEditConfig = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder liveEditConfig( final RootDataSet liveEditConfig )
        {
            this.liveEditConfig = liveEditConfig;
            return this;
        }

        public Builder pageTemplateId( final PageTemplateId pageTemplateId )
        {
            this.pageTemplateId = pageTemplateId;
            return this;
        }

        public Page build()
        {
            return new Page( this );
        }
    }
}
