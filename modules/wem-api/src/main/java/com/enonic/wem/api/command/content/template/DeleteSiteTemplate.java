package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public final class DeleteSiteTemplate
    extends Command<Boolean>
{
    private SiteTemplateKey siteTemplateKey;

    public DeleteSiteTemplate template( SiteTemplateKey siteTemplateKey )
    {
        this.siteTemplateKey = siteTemplateKey;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
