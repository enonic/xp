package com.enonic.xp.core.content.page.region;

public final class ImageComponentType
    extends ComponentType
{
    public final static ImageComponentType INSTANCE = new ImageComponentType();

    private ImageComponentType()
    {
        super( "image", ImageComponent.class );
    }

}
