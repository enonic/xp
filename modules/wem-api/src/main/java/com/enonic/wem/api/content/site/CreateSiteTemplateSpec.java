package com.enonic.wem.api.content.site;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.Templates;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class CreateSiteTemplateSpec
{
    private SiteTemplateName name;

    private SiteTemplateVersion version;

    private String displayName;

    private String description;

    private String url;

    private Vendor vendor;

    private ModuleKeys modules;

    private ContentTypeFilter contentTypeFilter;

    private ContentTypeName rootContentType;

    private List<Template> templates = Lists.newArrayList();

    public static CreateSiteTemplateSpec fromSiteTemplate( final SiteTemplate siteTemplate )
    {
        CreateSiteTemplateSpec createSiteTemplate = new CreateSiteTemplateSpec().
            name( siteTemplate.getName() ).
            version( siteTemplate.getVersion() ).
            displayName( siteTemplate.getDisplayName() ).
            description( siteTemplate.getDescription() ).
            url( siteTemplate.getUrl() ).
            vendor( siteTemplate.getVendor() ).
            modules( siteTemplate.getModules() ).
            contentTypeFilter( siteTemplate.getContentTypeFilter() ).
            rootContentType( siteTemplate.getRootContentType() );
        for ( Template template : siteTemplate )
        {
            createSiteTemplate.addTemplate( template );
        }
        return createSiteTemplate;
    }

    public CreateSiteTemplateSpec siteTemplateKey( final SiteTemplateKey key )
    {
        this.name( key.getName() );
        this.version( key.getVersion() );
        return this;
    }

    public CreateSiteTemplateSpec name( final String name )
    {
        return this.name( new SiteTemplateName( name ) );
    }

    public CreateSiteTemplateSpec name( final SiteTemplateName name )
    {
        this.name = name;
        return this;
    }

    public CreateSiteTemplateSpec version( final SiteTemplateVersion version )
    {
        this.version = version;
        return this;
    }

    public CreateSiteTemplateSpec displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateSiteTemplateSpec description( final String description )
    {
        this.description = description;
        return this;
    }

    public CreateSiteTemplateSpec url( final String url )
    {
        this.url = url;
        return this;
    }

    public CreateSiteTemplateSpec vendor( final Vendor vendor )
    {
        this.vendor = vendor;
        return this;
    }

    public CreateSiteTemplateSpec modules( final ModuleKeys modules )
    {
        this.modules = modules;
        return this;
    }

    public CreateSiteTemplateSpec contentTypeFilter( final ContentTypeFilter contentTypeFilter )
    {
        this.contentTypeFilter = contentTypeFilter;
        return this;
    }

    public CreateSiteTemplateSpec rootContentType( final ContentTypeName rootContentType )
    {
        this.rootContentType = rootContentType;
        return this;
    }

    public CreateSiteTemplateSpec addTemplate( final Template template )
    {
        this.templates.add( template );
        return this;
    }

    public CreateSiteTemplateSpec addTemplates( final Templates templates )
    {
        this.templates.addAll( templates.getList() );
        return this;
    }

    public SiteTemplateName getName()
    {
        return name;
    }

    public SiteTemplateVersion getVersion()
    {
        return version;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public String getUrl()
    {
        return url;
    }

    public Vendor getVendor()
    {
        return vendor;
    }

    public ModuleKeys getModules()
    {
        return modules;
    }

    public ContentTypeFilter getContentTypeFilter()
    {
        return contentTypeFilter;
    }

    public ContentTypeName getRootContentType()
    {
        return rootContentType;
    }

    public List<Template> getTemplates()
    {
        return templates;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.version, "version cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
    }
}
