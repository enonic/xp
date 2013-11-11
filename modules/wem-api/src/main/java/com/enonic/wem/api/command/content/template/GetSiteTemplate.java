package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateName;

public class GetSiteTemplate
    extends Command<SiteTemplate>
{
    private SiteTemplateName name;

    public GetSiteTemplate()
    {
    }

    public GetSiteTemplate templateName( final SiteTemplateName name )
    {
        this.name = name;
        return this;
    }

    public SiteTemplateName getName()
    {
        return name;
    }

    @Override
    public void validate()
    {

    }
}
