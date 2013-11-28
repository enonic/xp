package com.enonic.wem.admin.rest.resource.content.site.template.json;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplates;

public class ListSiteTemplateJson
{
    private List<SiteTemplateSummaryJson> list;

    public ListSiteTemplateJson( SiteTemplates siteTemplates )
    {
        ImmutableList.Builder<SiteTemplateSummaryJson> builder = ImmutableList.builder();
        for ( SiteTemplate siteTemplate : siteTemplates)
        {
            builder.add( new SiteTemplateSummaryJson( siteTemplate ));
        }
        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<SiteTemplateSummaryJson> getSiteTemplates()
    {
        return this.list;
    }
}
