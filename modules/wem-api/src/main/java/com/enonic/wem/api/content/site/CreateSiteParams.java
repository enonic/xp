package com.enonic.wem.api.content.site;


import com.enonic.wem.api.content.ContentId;

public final class CreateSiteParams
{
    private ContentId content;

    private SiteTemplateKey template;

    private ModuleConfigs moduleConfigs;

    public CreateSiteParams content( final ContentId value )
    {
        this.content = value;
        return this;
    }

    public CreateSiteParams template( final SiteTemplateKey value )
    {
        this.template = value;
        return this;
    }

    public CreateSiteParams moduleConfigs( final ModuleConfigs value )
    {
        this.moduleConfigs = value;
        return this;
    }

    public ContentId getContent()
    {
        return content;
    }

    public SiteTemplateKey getTemplate()
    {
        return template;
    }

    public ModuleConfigs getModuleConfigs()
    {
        return moduleConfigs;
    }
}
