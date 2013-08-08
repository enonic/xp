package com.enonic.wem.api.schema.content.form.inputtype.config;

import com.enonic.wem.api.schema.content.form.inputtype.ImageSelectorConfig;

public class ImageSelectorConfigJson
    extends AbstractInputTypeConfigJson
{
    private final ImageSelectorConfig config;

    public ImageSelectorConfigJson( final ImageSelectorConfig config )
    {
        this.config = config;
    }

    public String getRelationshipType()
    {
        return ( config.getRelationshipType() != null ) ? config.getRelationshipType().toString() : null;
    }
}
