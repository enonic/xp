package com.enonic.xp.region;

import com.google.common.annotations.Beta;

@Beta
public final class ImageComponentType
    extends ComponentType
{
    public final static ImageComponentType INSTANCE = new ImageComponentType();

    private ImageComponentType()
    {
        super( "image" );
    }
}
