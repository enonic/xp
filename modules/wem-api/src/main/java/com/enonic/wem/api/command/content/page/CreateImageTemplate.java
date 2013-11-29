package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateName;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;

public final class CreateImageTemplate
    extends Command<ImageTemplate>
{
    private SiteTemplateKey siteTemplate;

    private ImageTemplateName name;

    private String displayName;

    private ModuleResourceKey descriptor;

    private RootDataSet config;

    public CreateImageTemplate siteTemplate( final SiteTemplateKey value )
    {
        this.siteTemplate = value;
        return this;
    }

    public CreateImageTemplate templateName( final ImageTemplateName name )
    {
        this.name = name;
        return this;
    }

    public CreateImageTemplate displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateImageTemplate descriptor( final ModuleResourceKey descriptor )
    {
        this.descriptor = descriptor;
        return this;
    }

    public CreateImageTemplate config( final RootDataSet config )
    {
        this.config = config;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
