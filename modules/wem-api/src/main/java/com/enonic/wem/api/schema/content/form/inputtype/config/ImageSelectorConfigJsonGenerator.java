package com.enonic.wem.api.schema.content.form.inputtype.config;

import com.enonic.wem.api.schema.content.form.inputtype.ImageSelectorConfig;

public class ImageSelectorConfigJsonGenerator
    extends AbstractInputTypeConfigJsonGenerator<ImageSelectorConfig>
{
    public static final ImageSelectorConfigJsonGenerator DEFAULT = new ImageSelectorConfigJsonGenerator();

    @Override
    public AbstractInputTypeConfigJson generate( final ImageSelectorConfig config )
    {
        return new ImageSelectorConfigJson( config );
    }
}
