package com.enonic.wem.api.command.content.page.image;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class GetImageTemplateByKey
    extends Command<ImageTemplate>
{
    private SiteTemplateKey siteTemplateKey;

    private ImageTemplateKey key;

    public GetImageTemplateByKey siteTemplateKey( final SiteTemplateKey siteTemplateKey )
    {
        this.siteTemplateKey = siteTemplateKey;
        return this;
    }

    public GetImageTemplateByKey key( final ImageTemplateKey key )
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

    public ImageTemplateKey getKey()
    {
        return key;
    }
}
