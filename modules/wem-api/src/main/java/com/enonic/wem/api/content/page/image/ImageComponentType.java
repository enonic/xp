package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentJson;
import com.enonic.wem.api.content.page.PageComponentType;
import com.enonic.wem.api.content.page.PageComponentXml;

public final class ImageComponentType
    extends PageComponentType<ImageComponent>
{
    public ImageComponentType()
    {
        super( "image" );
    }

    @Override
    public PageComponentXml toXml( final PageComponent component )
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
}
