package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class GetPageTemplateByKey
    extends Command<PageTemplate>
{
    private SiteTemplateKey siteTemplateKey;

    private PageTemplateKey key;

    public GetPageTemplateByKey siteTemplateKey( final SiteTemplateKey siteTemplateKey )
    {
        this.siteTemplateKey = siteTemplateKey;
        return this;
    }

    public GetPageTemplateByKey key( final PageTemplateKey key )
    {
        this.key = key;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( siteTemplateKey, "siteTemplateKey is required" );
        Preconditions.checkNotNull( key, "key is required" );
    }

    public SiteTemplateKey getSiteTemplateKey()
    {
        return siteTemplateKey;
    }

    public PageTemplateKey getKey()
    {
        return key;
    }
}
