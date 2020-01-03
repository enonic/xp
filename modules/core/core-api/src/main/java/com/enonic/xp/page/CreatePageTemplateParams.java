package com.enonic.xp.page;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeNames;

@PublicApi
public class CreatePageTemplateParams
{
    private ContentPath site;

    private ContentName name;

    private String displayName;

    private DescriptorKey controller;

    private ContentTypeNames supports;

    private PageRegions pageRegions;

    private PropertyTree pageConfig;

    public CreatePageTemplateParams site( final ContentPath site )
    {
        this.site = site;
        return this;
    }

    public CreatePageTemplateParams name( final ContentName name )
    {
        this.name = name;
        return this;
    }

    public CreatePageTemplateParams name( final String name )
    {
        this.name = ContentName.from( name );
        return this;
    }

    public CreatePageTemplateParams displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreatePageTemplateParams controller( final DescriptorKey controller )
    {
        this.controller = controller;
        return this;
    }

    public CreatePageTemplateParams supports( final ContentTypeNames supports )
    {
        this.supports = supports;
        return this;
    }

    public CreatePageTemplateParams pageRegions( final PageRegions pageRegions )
    {
        this.pageRegions = pageRegions;
        return this;
    }

    public CreatePageTemplateParams pageConfig( final PropertyTree pageConfig )
    {
        this.pageConfig = pageConfig;
        return this;
    }

    public ContentPath getSite()
    {
        return site;
    }

    public ContentName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public DescriptorKey getController()
    {
        return controller;
    }

    public ContentTypeNames getSupports()
    {
        return supports;
    }

    public PageRegions getPageRegions()
    {
        return pageRegions;
    }

    public PropertyTree getPageConfig()
    {
        return pageConfig;
    }

    public void validate()
    {
        // TODO
    }
}
