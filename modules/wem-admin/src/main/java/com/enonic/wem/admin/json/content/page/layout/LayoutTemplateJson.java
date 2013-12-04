package com.enonic.wem.admin.json.content.page.layout;

import java.util.List;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;


public class LayoutTemplateJson
    extends LayoutTemplateSummaryJson
{
    private final RootDataSetJson configJson;

    public LayoutTemplateJson( final LayoutTemplate template )
    {
        super( template );
        this.configJson = new RootDataSetJson( template.getConfig() );
    }

    public List<DataJson> getConfig()
    {
        return configJson.getValue();
    }
}
