package com.enonic.xp.page;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.Component;

@PublicApi
public final class CreatePageParams
{
    private ContentId content;

    private DescriptorKey controller;

    private PageTemplateKey pageTemplate;

    private PageRegions regions;

    private Component fragment;

    private PropertyTree config;

    private boolean customized;

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

    public CreatePageParams fragment( Component fragment )
    {
        this.fragment = fragment;
        return this;
    }

    public CreatePageParams config( PropertyTree value )
    {
        this.config = value;
        return this;
    }

    public CreatePageParams customized( boolean value )
    {
        this.customized = value;
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

    public boolean isCustomized()
    {
        return customized;
    }

    public Component getFragment()
    {
        return fragment;
    }

    public void validate()
    {
        Objects.requireNonNull( this.content, "content is required" );
    }
}
