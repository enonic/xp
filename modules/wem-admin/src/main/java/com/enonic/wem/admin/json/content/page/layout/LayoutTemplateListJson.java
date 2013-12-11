package com.enonic.wem.admin.json.content.page.layout;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplates;

public class LayoutTemplateListJson
{
    private List<LayoutTemplateSummaryJson> list;

    public LayoutTemplateListJson( LayoutTemplates layoutTemplates )
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

    public List<LayoutTemplateSummaryJson> getTemplates()
    {
        return this.list;
    }
}
