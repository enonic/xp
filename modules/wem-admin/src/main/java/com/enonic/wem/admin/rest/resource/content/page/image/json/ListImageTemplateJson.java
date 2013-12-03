package com.enonic.wem.admin.rest.resource.content.page.image.json;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplates;

public class ListImageTemplateJson
{
    private List<ImageTemplateSummaryJson> list;

    public ListImageTemplateJson( ImageTemplates imageTemplates )
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

    public List<ImageTemplateSummaryJson> getSiteTemplates()
    {
        return this.list;
    }
}
