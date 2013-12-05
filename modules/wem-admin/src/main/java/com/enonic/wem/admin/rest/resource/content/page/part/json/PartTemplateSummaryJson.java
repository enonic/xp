package com.enonic.wem.admin.rest.resource.content.page.part.json;

import com.enonic.wem.admin.json.content.page.TemplateSummaryJson;
import com.enonic.wem.api.content.page.part.PartTemplate;

public class PartTemplateSummaryJson extends TemplateSummaryJson
{

    private PartTemplate partTemplate;

    private boolean deletable;

    private boolean editable;

    public PartTemplateSummaryJson( PartTemplate partTemplate )
    {
        super(partTemplate);
        this.partTemplate = partTemplate;
    }

}
