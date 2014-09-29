package com.enonic.wem.api.content.page.image;

import com.enonic.wem.api.content.page.PageComponentType;

public final class ImageComponentType
    extends PageComponentType
{
    public final static ImageComponentType INSTANCE = new ImageComponentType();

    private static final ImageComponentDataSerializer dataSerializer = new ImageComponentDataSerializer();

    private ImageComponentType()
    {
        super( "image", ImageComponent.class );
    }

    @Override
    public ImageComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
