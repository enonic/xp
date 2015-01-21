package com.enonic.wem.api.content.page.region;

public final class ImageComponentType
    extends ComponentType
{
    public final static ImageComponentType INSTANCE = new ImageComponentType();

    private ImageComponentType()
    {
        super( "image", ImageComponent.class );
    }

}
