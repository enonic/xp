package com.enonic.wem.admin.json.content.site;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.enonic.wem.admin.json.content.page.PageTemplateJson;
import com.enonic.wem.admin.rest.resource.content.site.template.SiteTemplateIconUrlResolver;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;

public final class SiteTemplateJson
    extends SiteTemplateSummaryJson
{
    public SiteTemplateJson( final SiteTemplate siteTemplate, final SiteTemplateIconUrlResolver iconUrlResolver )
    {
        super( siteTemplate, iconUrlResolver );
    }

    public List<PageTemplateJson> getPageTemplates()
    {
        return templatesAsJsonList( siteTemplate.getPageTemplates().getList() );
    }

    private List<PageTemplateJson> templatesAsJsonList( final List<PageTemplate> templateList )
    {
        return Lists.transform( templateList, new Function<PageTemplate, PageTemplateJson>()
        {
            @Override
            public PageTemplateJson apply( final PageTemplate template )
            {
                return new PageTemplateJson( template );
            }
        } );
    }
}
