package com.enonic.wem.api.command.content.site;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class CreateSite
    extends Command<Content>
{
    private ContentId content;

    private SiteTemplateKey template;

    private ModuleConfigs moduleConfigs;

    public CreateSite content( final ContentId value )
    {
        this.content = value;
        return this;
    }

    public CreateSite template( final SiteTemplateKey value )
    {
        this.template = value;
        return this;
    }

    public CreateSite moduleConfigs( final ModuleConfigs value )
    {
        this.moduleConfigs = value;
        return this;
    }

    @Override
    public void validate()
    {

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
