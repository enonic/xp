package com.enonic.wem.admin.rest.resource.content.site.template.json;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class SiteTemplateSummaryJson
    implements ItemJson
{
    private SiteTemplate siteTemplate;

    public SiteTemplateSummaryJson( SiteTemplate siteTemplate )
    {
        this.siteTemplate = siteTemplate;
    }

    public String getKey()
    {
        return this.siteTemplate.getKey().toString();
    }

    public String getName()
    {
        return this.siteTemplate.getName().toString();
    }

    public String getVersion()
    {
        return this.siteTemplate.getVersion().toString();
    }

    public String getDisplayName()
    {
        return this.siteTemplate.getDisplayName();
    }

    public String getInfo()
    {
        return this.siteTemplate.getDescription();
    }

    public String getUrl()
    {
        return this.siteTemplate.getUrl();
    }

    public VendorJson getVendor()
    {
        return new VendorJson( this.siteTemplate.getVendor() );
    }

    public List<String> getModules()
    {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for ( ModuleKey moduleKey : this.siteTemplate.getModules() )
        {
            builder.add( moduleKey.toString() );
        }
        return builder.build();
    }

    public Map<String, Boolean> getContentTypeFilter()
    {
        ContentTypeFilter contentTypeFilter = this.siteTemplate.getContentTypeFilter();
        ImmutableMap.Builder<String, Boolean> accessTable = ImmutableMap.builder();
        for ( ContentTypeName contentTypeName : contentTypeFilter )
        {
            accessTable.put( contentTypeName.getContentTypeName(), contentTypeFilter.isContentTypeAllowed( contentTypeName ) );
        }

        return accessTable.build();
    }

    public String getRootContentType()
    {
        return this.siteTemplate.getRootContentType().getContentTypeName();
    }

    @Override
    public boolean getEditable()
    {
        return true;
    }

    @Override
    public boolean getDeletable()
    {
        return true;
    }
}
