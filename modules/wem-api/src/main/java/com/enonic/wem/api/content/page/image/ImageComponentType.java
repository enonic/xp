package com.enonic.wem.api.content.page.image;

import com.enonic.wem.api.content.page.PageComponentType;

public final class ImageComponentType
    extends PageComponentType
{
    private static final ImageComponentDataSerializer dataSerializer = new ImageComponentDataSerializer();

    public ImageComponentType()
    {
        super( "image", ImageComponent.class );
    }

    @Override
    public ImageComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
