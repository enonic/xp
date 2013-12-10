package com.enonic.wem.admin.json.content.page.image;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplates;

public class ImageTemplateListJson
{
    private List<ImageTemplateSummaryJson> list;

    public ImageTemplateListJson( ImageTemplates imageTemplates )
    {
        ImmutableList.Builder<ImageTemplateSummaryJson> builder = ImmutableList.builder();
        for ( ImageTemplate imageTemplate : imageTemplates )
        {
            builder.add( new ImageTemplateSummaryJson( imageTemplate ) );
        }
        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<ImageTemplateSummaryJson> getTemplates()
    {
        return this.list;
    }
}
