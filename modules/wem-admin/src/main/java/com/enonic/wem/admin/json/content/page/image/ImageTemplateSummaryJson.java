package com.enonic.wem.admin.json.content.page.image;

import com.enonic.wem.admin.json.content.page.TemplateSummaryJson;
import com.enonic.wem.api.content.page.image.ImageTemplate;


public class ImageTemplateSummaryJson
    extends TemplateSummaryJson
{
    protected final ImageTemplate imageTemplate;

    public ImageTemplateSummaryJson( final ImageTemplate imageTemplate )
    {
        super( imageTemplate );
        this.imageTemplate = imageTemplate;
    }
}
