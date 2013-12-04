package com.enonic.wem.admin.json.content.page;

import com.enonic.wem.api.content.page.PageTemplate;


public class PageTemplateSummaryJson
    extends TemplateSummaryJson
{
    protected final PageTemplate partTemplate;

    public PageTemplateSummaryJson( final PageTemplate template )
    {
        super( template );
        this.partTemplate = template;
    }
}
