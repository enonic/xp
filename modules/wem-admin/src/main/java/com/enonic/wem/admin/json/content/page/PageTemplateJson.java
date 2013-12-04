package com.enonic.wem.admin.json.content.page;

import java.util.List;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.PageTemplate;


public class PageTemplateJson
    extends PageTemplateSummaryJson
{
    private final RootDataSetJson configJson;

    public PageTemplateJson( final PageTemplate template )
    {
        super( template );
        this.configJson = new RootDataSetJson( template.getConfig() );
    }

    public List<DataJson> getConfig()
    {
        return configJson.getSet();
    }
}
