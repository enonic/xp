package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.page.DescriptorBasedPageComponentType;

public final class ImageComponentType
    extends DescriptorBasedPageComponentType<ImageComponent>
{
    private static final ImageComponentDataSerializer dataSerializer = new ImageComponentDataSerializer();

    public ImageComponentType()
    {
        super( "image", ImageComponent.class );
    }

    @Override
    public ImageComponentXml toXml( final ImageComponent component )
    {
        ImageComponentXml componentXml = new ImageComponentXml();
        componentXml.from( component );
        return componentXml;
    }

    @Override
    public ImageComponentJson toJson( final ImageComponent component )
    {
        return new ImageComponentJson( component );
    }

    @Override
    public ImageComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
