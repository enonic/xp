package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.LayoutTemplate;
import com.enonic.wem.api.content.page.LayoutTemplateName;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;

public final class CreateLayoutTemplate
    extends Command<LayoutTemplate>
{
    private SiteTemplateKey siteTemplate;

    private LayoutTemplateName name;

    private String displayName;

    private ModuleResourceKey descriptor;

    private RootDataSet config;

    public CreateLayoutTemplate siteTemplate( final SiteTemplateKey value )
    {
        this.siteTemplate = value;
        return this;
    }

    public CreateLayoutTemplate templateName( final LayoutTemplateName name )
    {
        this.name = name;
        return this;
    }

    public CreateLayoutTemplate displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateLayoutTemplate descriptor( final ModuleResourceKey descriptor )
    {
        this.descriptor = descriptor;
        return this;
    }

    public CreateLayoutTemplate config( final RootDataSet config )
    {
        this.config = config;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
