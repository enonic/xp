package com.enonic.wem.api.command.content.site;

import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateKeys;

public class SiteTemplateGetCommands
{
    public GetAllSiteTemplates all()
    {
        return new GetAllSiteTemplates();
    }

    public GetSiteTemplateByKey byKey( final SiteTemplateKey key )
    {
        return new GetSiteTemplateByKey( key );
    }

    public GetSiteTemplateByKeys byKeys( final SiteTemplateKeys keys )
    {
        return new GetSiteTemplateByKeys( keys );
    }

    public DeleteSite delete()
    {
        return new DeleteSite();
    }
}
