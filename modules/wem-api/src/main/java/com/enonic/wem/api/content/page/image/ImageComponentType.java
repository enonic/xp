package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.page.AbstractPageComponentXml;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentDataSerializer;
import com.enonic.wem.api.content.page.PageComponentJson;
import com.enonic.wem.api.content.page.PageComponentType;

public final class ImageComponentType
    extends PageComponentType<ImageComponent>
{
    private static final ImageComponentDataSerializer dataSerializer = new ImageComponentDataSerializer();

    public ImageComponentType()
    {
        super( "image", ImageComponent.class );
    }

    @Override
    public AbstractPageComponentXml toXml( final PageComponent component )
    {
        ImageComponentXml componentXml = new ImageComponentXml();
        componentXml.from( (ImageComponent) component );
        return componentXml;
    }

    @Override
    public PageComponentJson toJson( final ImageComponent component )
    {
        return new ImageComponentJson( component );
    }

    @Override
    public PageComponentDataSerializer getDataSerializer()
    {
        return dataSerializer;
    }
}
