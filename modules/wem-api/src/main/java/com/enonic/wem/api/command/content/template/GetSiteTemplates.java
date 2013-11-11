package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.site.SiteTemplateNames;
import com.enonic.wem.api.content.site.SiteTemplates;

public final class GetSiteTemplates
    extends Command<SiteTemplates>
{
    private boolean getAllTemplates = false;

    private SiteTemplateNames templateNames;

    public GetSiteTemplates templates( final SiteTemplateNames templateNames )
    {
        this.templateNames = templateNames;
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

    public SiteTemplateNames getTemplates()
    {
        return templateNames;
    }

    @Override
    public void validate()
    {

    }
}
