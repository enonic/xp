package com.enonic.wem.api.content.page.region;

public final class ImageComponentType
    extends ComponentType
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
