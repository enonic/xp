package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateName;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;

public final class CreatePartTemplate
    extends Command<PartTemplate>
{
    private SiteTemplateKey siteTemplate;

    private PartTemplateName name;

    private String displayName;

    private ModuleResourceKey descriptor;

    private RootDataSet config;

    public CreatePartTemplate siteTemplate( final SiteTemplateKey value )
    {
        this.siteTemplate = value;
        return this;
    }

    public CreatePartTemplate templateName( final PartTemplateName name )
    {
        this.name = name;
        return this;
    }

    public CreatePartTemplate displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreatePartTemplate descriptor( final ModuleResourceKey descriptor )
    {
        this.descriptor = descriptor;
        return this;
    }

    public CreatePartTemplate config( final RootDataSet config )
    {
        this.config = config;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
