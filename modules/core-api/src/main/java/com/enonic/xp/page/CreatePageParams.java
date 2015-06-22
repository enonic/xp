package com.enonic.xp.page;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyTree;

@Beta
public final class CreatePageParams
{
    private ContentId content;

    private DescriptorKey controller;

    private PageTemplateKey pageTemplate;

    private PageRegions regions;

    private PropertyTree config;

    public CreatePageParams content( ContentId value )
    {
        this.content = value;
        return this;
    }

    public CreatePageParams controller( DescriptorKey value )
    {
        this.controller = value;
        return this;
    }

    public CreatePageParams pageTemplate( PageTemplateKey value )
    {
        this.pageTemplate = value;
        return this;
    }

    public CreatePageParams regions( PageRegions value )
    {
        this.regions = value;
        return this;
    }

    public CreatePageParams config( PropertyTree value )
    {
        this.config = value;
        return this;
    }

    public ContentId getContent()
    {
        return content;
    }

    public PageTemplateKey getPageTemplate()
    {
        return pageTemplate;
    }

    public DescriptorKey getController()
    {
        return controller;
    }

    public PageRegions getRegions()
    {
        return regions;
    }

    public PropertyTree getConfig()
    {
        return config;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.content, "content cannot be null" );
    }
}
