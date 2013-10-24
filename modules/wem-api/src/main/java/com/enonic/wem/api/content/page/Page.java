package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;

public final class Page
    extends PageComponent<PageTemplateId>
{
    private final RootDataSet config;

    private Page( final Builder builder )
    {
        super( builder.pageTemplateId );
        this.config = builder.config;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public static Builder newPage()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RootDataSet config;

        private PageTemplateId pageTemplateId;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
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
