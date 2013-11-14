package com.enonic.wem.api.command.content.site;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public final class GetSiteTemplateByKey
    extends Command<SiteTemplate>
{
    private SiteTemplateKey key;

    public GetSiteTemplateByKey( final SiteTemplateKey key )
    {
        this.key = key;
    }

    public SiteTemplateKey getKey()
    {
        return key;
    }

    @Override
    public void validate()
    {

    }
}
