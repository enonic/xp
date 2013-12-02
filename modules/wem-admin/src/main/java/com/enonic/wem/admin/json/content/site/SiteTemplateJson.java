package com.enonic.wem.admin.json.content.site;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.site.SiteTemplate;

public final class SiteTemplateJson
    extends SiteTemplateSummaryJson
{
    public SiteTemplateJson( final SiteTemplate siteTemplate )
    {
        super( siteTemplate );
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
