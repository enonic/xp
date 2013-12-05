package com.enonic.wem.admin.json.content.page;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.schema.content.ContentTypeName;


public class PageTemplateJson
    extends PageTemplateSummaryJson
{
    private final RootDataSetJson configJson;

    private final ImmutableList<String> canRender;

    public PageTemplateJson( final PageTemplate template )
    {
        super( template );
        this.configJson = new RootDataSetJson( template.getConfig() );
        ImmutableList.Builder<String> canRenderBuilder = new ImmutableList.Builder<>();
        for ( ContentTypeName contentTypeName : template.getCanRender() )
        {
            canRenderBuilder.add( contentTypeName.toString() );
        }
        this.canRender = canRenderBuilder.build();
    }

    public List<DataJson> getConfig()
    {
        return configJson.getSet();
    }

    public List<String> getCanRender()
    {
        return canRender;
    }
}
