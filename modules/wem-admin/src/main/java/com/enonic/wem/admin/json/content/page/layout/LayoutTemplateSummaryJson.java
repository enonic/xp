package com.enonic.wem.admin.json.content.page.layout;

import com.enonic.wem.admin.json.content.page.TemplateSummaryJson;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;


public class LayoutTemplateSummaryJson
    extends TemplateSummaryJson
{
    protected final LayoutTemplate layoutTemplate;

    public LayoutTemplateSummaryJson( final LayoutTemplate template )
    {
        super( template );
        this.layoutTemplate = template;
    }
}
