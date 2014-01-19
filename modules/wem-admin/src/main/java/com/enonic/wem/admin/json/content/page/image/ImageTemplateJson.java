package com.enonic.wem.admin.json.content.page.image;

import java.util.List;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.image.ImageTemplate;


public class ImageTemplateJson
    extends ImageTemplateSummaryJson
{
    private final RootDataSetJson configJson;

    private final ImageDescriptorJson descriptorJson;

    private final ImageTemplate imageTemplate;

    public ImageTemplateJson( final ImageTemplate template )
    {
        this( template, null );
    }

    public ImageTemplateJson( final ImageTemplate template, final ImageDescriptorJson descriptorJson )
    {
        super( template );
        this.imageTemplate = template;
        this.configJson = new RootDataSetJson( imageTemplate.getConfig() );
        this.descriptorJson = descriptorJson;
    }

    public List<DataJson> getConfig()
    {
        return configJson.getSet();
    }

    public String getImage()
    {
        return imageTemplate.getImage() != null ? imageTemplate.getImage().toString() : null;
    }

    public ImageDescriptorJson getDescriptor()
    {
        return descriptorJson;
    }
}
