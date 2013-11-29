package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;

public final class Page
    extends BasePageComponent<PageTemplateName>
{
    private final RootDataSet config;

    private Page( final Builder builder )
    {
        super( builder.pageTemplateName );
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

        private PageTemplateName pageTemplateName;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder pageTemplateName( final PageTemplateName pageTemplateName )
        {
            this.pageTemplateName = pageTemplateName;
            return this;
        }

        public Page build()
        {
            return new Page( this );
        }
    }
}
