package com.enonic.wem.admin.json.content.page;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.json.content.page.region.PageRegionsJson;
import com.enonic.wem.admin.json.content.page.region.RegionJson;
import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.schema.content.ContentTypeName;


public class PageTemplateJson
    extends PageTemplateSummaryJson
{
    private final RootDataSetJson configJson;

    private final PageRegionsJson regionsJson;

    private final ImmutableList<String> canRender;

    private final PageDescriptorJson descriptorJson;

    public PageTemplateJson( final PageTemplate template )
    {
        this( template, null );
    }

    public PageTemplateJson( final PageTemplate template, final PageDescriptorJson descriptorJson )
    {
        super( template );
        this.regionsJson = new PageRegionsJson( template.getRegions() );
        this.configJson = new RootDataSetJson( template.getConfig() );
        ImmutableList.Builder<String> canRenderBuilder = new ImmutableList.Builder<>();
        for ( ContentTypeName contentTypeName : template.getCanRender() )
        {
            canRenderBuilder.add( contentTypeName.toString() );
        }
        this.canRender = canRenderBuilder.build();
        this.descriptorJson = descriptorJson;
    }

    public List<RegionJson> getRegions()
    {
        return regionsJson.getRegions();
    }

    public List<DataJson> getConfig()
    {
        return configJson.getSet();
    }

    public List<String> getCanRender()
    {
        return canRender;
    }

    public PageDescriptorJson getDescriptor()
    {
        return descriptorJson;
    }
}
