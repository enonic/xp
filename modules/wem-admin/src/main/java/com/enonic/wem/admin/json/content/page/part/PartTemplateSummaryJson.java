package com.enonic.wem.admin.json.content.page.part;

import com.enonic.wem.admin.json.content.page.TemplateSummaryJson;
import com.enonic.wem.api.content.page.part.PartTemplate;


public class PartTemplateSummaryJson
    extends TemplateSummaryJson
{
    protected final PartTemplate partTemplate;

    public PartTemplateSummaryJson( final PartTemplate template )
    {
        super( template );
        this.partTemplate = template;
    }
}
