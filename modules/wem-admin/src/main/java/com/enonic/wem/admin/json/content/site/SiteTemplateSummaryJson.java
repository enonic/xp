package com.enonic.wem.admin.json.content.site;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.VendorJson;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.module.ModuleKey;


public class SiteTemplateSummaryJson
    implements ItemJson
{
    protected final SiteTemplate siteTemplate;

    private ContentTypeFilterJson contentTypeFilterJson;

    private final boolean editable;

    private final boolean deletable;

    public SiteTemplateSummaryJson( final SiteTemplate siteTemplate )
    {
        this.siteTemplate = siteTemplate;
        this.editable = true;
        this.deletable = true;
        this.contentTypeFilterJson = new ContentTypeFilterJson( siteTemplate.getContentTypeFilter() );
    }

    public String getKey()
    {
        return siteTemplate.getKey().toString();
    }

    public String getDisplayName()
    {
        return siteTemplate.getDisplayName();
    }

    public String getName()
    {
        return siteTemplate.getName().toString();
    }

    public String getDescription()
    {
        return siteTemplate.getDescription();
    }

    public String getUrl()
    {
        return siteTemplate.getUrl();
    }

    public String getRootContentType()
    {
        return siteTemplate.getRootContentType().getContentTypeName();
    }

    public VendorJson getVendor()
    {
        return new VendorJson( siteTemplate.getVendor() );
    }

    public String getVersion()
    {
        return siteTemplate.getVersion().toString();
    }

    public List<String> getModules()
    {
        return Lists.transform( siteTemplate.getModules().getList(), new Function<ModuleKey, String>()
        {
            @Override
            public String apply( final ModuleKey moduleKey )
            {
                return moduleKey.toString();
            }
        } );
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }


    public ContentTypeFilterJson getContentTypeFilter()
    {
        return contentTypeFilterJson;
    }

    public List<String> getPageTemplates()
    {
        return templatesAsNameList( siteTemplate.getPageTemplates().getList() );
    }

    public List<String> getPartTemplates()
    {
        return templatesAsNameList( siteTemplate.getPartTemplates().getList() );
    }

    public List<String> getLayoutTemplates()
    {
        return templatesAsNameList( siteTemplate.getLayoutTemplates().getList() );
    }

    public List<String> getImageTemplates()
    {
        return templatesAsNameList( siteTemplate.getImageTemplates().getList() );
    }

    private List<String> templatesAsNameList( final List<? extends Template> templateList )
    {
        return Lists.transform( templateList, new Function<Template, String>()
        {
            @Override
            public String apply( final Template template )
            {
                return template.getName().toString();
            }
        } );
    }

}
