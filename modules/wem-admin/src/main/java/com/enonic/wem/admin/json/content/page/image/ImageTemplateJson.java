package com.enonic.wem.admin.json.content.page.image;

import java.util.List;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.image.ImageTemplate;


public class ImageTemplateJson
    extends ImageTemplateSummaryJson
{
    private final RootDataSetJson configJson;

    public ImageTemplateJson( final ImageTemplate imageTemplate )
    {
        super( imageTemplate );
        this.configJson = new RootDataSetJson( imageTemplate.getConfig() );
    }

    public List<DataJson> getConfig()
    {
        return configJson.getSet();
    }
}
