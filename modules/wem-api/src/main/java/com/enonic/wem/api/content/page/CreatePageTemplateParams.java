package com.enonic.wem.api.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public final class CreatePageTemplateParams
    extends Command<PageTemplate>
{
    private SiteTemplateKey siteTemplate;

    private PageTemplateName name;

    private String displayName;

    private ModuleResourceKey descriptor;

    private RootDataSet config;

    private ContentTypeNames canRender;

    public CreatePageTemplateParams siteTemplate( final SiteTemplateKey value )
    {
        this.siteTemplate = value;
        return this;
    }

    public CreatePageTemplateParams name( final PageTemplateName name )
    {
        this.name = name;
        return this;
    }

    public CreatePageTemplateParams displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreatePageTemplateParams descriptor( final ModuleResourceKey descriptor )
    {
        this.descriptor = descriptor;
        return this;
    }

    public CreatePageTemplateParams config( final RootDataSet config )
    {
        this.config = config;
        return this;
    }

    public CreatePageTemplateParams canRender( final ContentTypeNames canRender )
    {
        this.canRender = canRender;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
