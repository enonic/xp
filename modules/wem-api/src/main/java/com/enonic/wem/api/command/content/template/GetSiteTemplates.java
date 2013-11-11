package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.site.SiteTemplateKeys;
import com.enonic.wem.api.content.site.SiteTemplates;

public final class GetSiteTemplates
    extends Command<SiteTemplates>
{
    private boolean getAllTemplates = false;

    private SiteTemplateKeys templateKeys;

    public GetSiteTemplates templates( final SiteTemplateKeys templateKeys )
    {
        this.templateKeys = templateKeys;
        return this;
    }

    public GetSiteTemplates all()
    {
        getAllTemplates = true;
        return this;
    }

    boolean isGetAll()
    {
        return getAllTemplates;
    }

    public SiteTemplateKeys getTemplates()
    {
        return templateKeys;
    }

    @Override
    public void validate()
    {

    }
}
