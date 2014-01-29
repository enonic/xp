package com.enonic.wem.api.command.content.page.layout;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class GetLayoutTemplateByKey
    extends Command<LayoutTemplate>
{
    private SiteTemplateKey siteTemplateKey;

    private LayoutTemplateKey key;

    public GetLayoutTemplateByKey siteTemplateKey( final SiteTemplateKey siteTemplateKey )
    {
        this.siteTemplateKey = siteTemplateKey;
        return this;
    }

    public GetLayoutTemplateByKey key( final LayoutTemplateKey key )
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

    public LayoutTemplateKey getKey()
    {
        return key;
    }
}
