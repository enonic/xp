package com.enonic.wem.admin.json.content.site;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.VendorJson;
import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.module.ModuleKey;


public class SiteTemplateSummaryJson
    implements ItemJson
{
    protected final SiteTemplate siteTemplate;

    private final boolean editable;

    private final boolean deletable;

    public SiteTemplateSummaryJson( final SiteTemplate siteTemplate )
    {
        this.siteTemplate = siteTemplate;
        this.editable = true;
        this.deletable = true;
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

    public String getSiteContent()
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

    public ContentTypeFilterJson getContentFilter()
    {
        final ContentTypeFilter contentTypeFilter = siteTemplate.getContentTypeFilter();
        return contentTypeFilter == null ? null : new ContentTypeFilterJson( contentTypeFilter );
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

}
