package com.enonic.wem.admin.rest.resource.content.page.layout.json;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.json.content.page.layout.LayoutTemplateSummaryJson;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplates;

public class ListLayoutTemplateJson
{
    private List<LayoutTemplateSummaryJson> list;

    public ListLayoutTemplateJson( LayoutTemplates layoutTemplates )
    {
        ImmutableList.Builder<LayoutTemplateSummaryJson> builder = ImmutableList.builder();
        for ( LayoutTemplate layoutTemplate : layoutTemplates )
        {
            builder.add( new LayoutTemplateSummaryJson( layoutTemplate ) );
        }
        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<LayoutTemplateSummaryJson> getLayoutTemplates()
    {
        return this.list;
    }
}
