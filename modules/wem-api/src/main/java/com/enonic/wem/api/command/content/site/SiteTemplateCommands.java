package com.enonic.wem.api.command.content.site;

import com.enonic.wem.api.content.site.SiteTemplateKey;

public class SiteTemplateCommands
{
    public CreateSiteTemplate create()
    {
        return new CreateSiteTemplate();
    }

    public UpdateSiteTemplate update()
    {
        return new UpdateSiteTemplate();
    }

    public DeleteSiteTemplate delete( final SiteTemplateKey key )
    {
        return new DeleteSiteTemplate( key );
    }

    public SiteTemplateGetCommands get()
    {
        return new SiteTemplateGetCommands();
    }
}
