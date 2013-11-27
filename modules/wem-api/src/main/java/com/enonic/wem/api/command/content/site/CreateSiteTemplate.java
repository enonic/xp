package com.enonic.wem.api.command.content.site;


import java.util.HashMap;
import java.util.Map;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.Vendor;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public class CreateSiteTemplate
    extends Command<SiteTemplate>
{
    private SiteTemplateKey siteTemplateKey;

    private String displayName;

    private String description;

    private Vendor vendor;

    private ModuleKeys modules;

    private ContentTypeNames supportedContentTypes;

    private ContentTypeName rootContentType;

    private Map<ResourcePath, Template> templates = new HashMap<>();

    public CreateSiteTemplate siteTemplateKey( final SiteTemplateKey siteTemplateKey )
    {
        this.siteTemplateKey = siteTemplateKey;
        return this;
    }

    public CreateSiteTemplate displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateSiteTemplate description( final String description )
    {
        this.description = description;
        return this;
    }

    public CreateSiteTemplate vendor( final Vendor vendor )
    {
        this.vendor = vendor;
        return this;
    }

    public CreateSiteTemplate modules( final ModuleKeys modules )
    {
        this.modules = modules;
        return this;
    }

    public CreateSiteTemplate supportedContentTypes( final ContentTypeNames supportedContentTypes )
    {
        this.supportedContentTypes = supportedContentTypes;
        return this;
    }

    public CreateSiteTemplate rootContentType( final ContentTypeName rootContentType )
    {
        this.rootContentType = rootContentType;
        return this;
    }

    public CreateSiteTemplate addTemplate( final ResourcePath resourcePath, Template template )
    {
        this.templates.put( resourcePath, template );
        return this;
    }

    public SiteTemplateKey getSiteTemplateKey()
    {
        return siteTemplateKey;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public Vendor getVendor()
    {
        return vendor;
    }

    public ModuleKeys getModules()
    {
        return modules;
    }

    public ContentTypeNames getSupportedContentTypes()
    {
        return supportedContentTypes;
    }

    public ContentTypeName getRootContentType()
    {
        return rootContentType;
    }

    public Map<ResourcePath, Template> getTemplates()
    {
        return templates;
    }

    @Override
    public void validate()
    {
    }
}
