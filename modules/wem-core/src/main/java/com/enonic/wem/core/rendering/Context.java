package com.enonic.wem.core.rendering;


import com.enonic.wem.api.content.site.Site;

public class Context
{
    private Site site;

    private Context( Builder builder )
    {
        this.site = builder.site;
    }

    public Site getSite()
    {
        return site;
    }

    public static Builder newContext()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Site site;

        public Builder site( Site site )
        {
            this.site = site;
            return this;
        }

        public Context build()
        {
            return new Context( this );
        }
    }
}
