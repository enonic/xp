package com.enonic.wem.admin.json.content.page;

import com.enonic.wem.api.content.page.PageTemplate;


public class PageTemplateSummaryJson
    extends TemplateSummaryJson
{
    protected final PageTemplate pageTemplate;

    public PageTemplateSummaryJson( final PageTemplate template )
    {
        super( template );
        this.pageTemplate = template;
    }
}
