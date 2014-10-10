package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.RootDataSet;

public final class CreatePageParams
{
    private ContentId content;

    private PageDescriptorKey controller;

    private PageTemplateKey pageTemplate;

    private PageRegions regions;

    private RootDataSet config;

    public CreatePageParams content( ContentId value )
    {
        this.content = value;
        return this;
    }

    public CreatePageParams controller( PageDescriptorKey value )
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

    public CreatePageParams config( RootDataSet value )
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

    public PageDescriptorKey getController()
    {
        return controller;
    }

    public PageRegions getRegions()
    {
        return regions;
    }

    public RootDataSet getConfig()
    {
        return config;
    }
}
