package com.enonic.wem.admin.json.content.page.part;

import java.util.List;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.part.PartTemplate;


public class PartTemplateJson
    extends PartTemplateSummaryJson
{
    private final RootDataSetJson configJson;

    public PartTemplateJson( final PartTemplate template )
    {
        super( template );
        this.configJson = new RootDataSetJson( template.getConfig() );
    }

    public List<DataJson> getConfig()
    {
        return configJson.getValue();
    }
}
