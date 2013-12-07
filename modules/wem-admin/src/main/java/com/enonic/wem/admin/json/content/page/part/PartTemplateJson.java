package com.enonic.wem.admin.json.content.page.part;

import java.util.List;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.part.PartTemplate;


public class PartTemplateJson
    extends PartTemplateSummaryJson
{
    private final RootDataSetJson configJson;

    private final PartDescriptorJson descriptorJson;

    public PartTemplateJson( final PartTemplate template )
    {
        this( template, null );
    }

    public PartTemplateJson( final PartTemplate template, final PartDescriptorJson descriptorJson )
    {
        super( template );
        this.configJson = new RootDataSetJson( template.getConfig() );
        this.descriptorJson = descriptorJson;
    }

    public List<DataJson> getConfig()
    {
        return configJson.getSet();
    }

    public PartDescriptorJson getDescriptor()
    {
        return descriptorJson;
    }
}
