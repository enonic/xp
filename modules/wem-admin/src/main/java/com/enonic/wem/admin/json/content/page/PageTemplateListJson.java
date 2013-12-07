package com.enonic.wem.admin.json.content.page;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplates;

public class PageTemplateListJson
{
    private final ImmutableList<PageTemplateSummaryJson> templates;

    public PageTemplateListJson( final PageTemplates pageTemplates )
    {
        final ImmutableList.Builder<PageTemplateSummaryJson> builder = ImmutableList.builder();
        for ( final PageTemplate pageTemplate : pageTemplates )
        {
            builder.add( new PageTemplateSummaryJson( pageTemplate ) );
        }
        this.templates = builder.build();
    }

    public ImmutableList<PageTemplateSummaryJson> getTemplates()
    {
        return templates;
    }
}
