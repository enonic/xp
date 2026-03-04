package com.enonic.xp.region;

public final class ImageComponentType
    extends ComponentType
{
    public static final ImageComponentType INSTANCE = new ImageComponentType();

    private ImageComponentType()
    {
        super( "image" );
    }
}
