package com.enonic.wem.api.command.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class GetPartTemplateByKey
    extends Command<PartTemplate>
{
    private SiteTemplateKey siteTemplateKey;

    private PartTemplateKey key;

    public GetPartTemplateByKey siteTemplateKey( final SiteTemplateKey siteTemplateKey )
    {
        this.siteTemplateKey = siteTemplateKey;
        return this;
    }

    public GetPartTemplateByKey key( final PartTemplateKey key )
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

    public PartTemplateKey getKey()
    {
        return key;
    }
}
