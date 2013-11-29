package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public final class CreatePageTemplate
    extends Command<PageTemplate>
{
    private SiteTemplateKey siteTemplate;

    private PageTemplateName name;

    private String displayName;

    private ModuleResourceKey descriptor;

    private RootDataSet config;

    private ContentTypeNames canRender;

    public CreatePageTemplate siteTemplate( final SiteTemplateKey value )
    {
        this.siteTemplate = value;
        return this;
    }

    public CreatePageTemplate name( final PageTemplateName name )
    {
        this.name = name;
        return this;
    }

    public CreatePageTemplate displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreatePageTemplate descriptor( final ModuleResourceKey descriptor )
    {
        this.descriptor = descriptor;
        return this;
    }

    public CreatePageTemplate config( final RootDataSet config )
    {
        this.config = config;
        return this;
    }

    public CreatePageTemplate canRender( final ContentTypeNames canRender )
    {
        this.canRender = canRender;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
