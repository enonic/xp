package com.enonic.xp.image;

import java.io.IOException;

import com.google.common.io.ByteSource;

import com.enonic.xp.style.ImageStyle;

public interface ImageService
{
    /**
     * Resolves a predefined processing style by its application:name key.
     * Throws ImageStyleNotFoundException for an unknown style, or IllegalArgumentException for invalid style settings.
     */
    ImageStyle getStyle( String key );

    ByteSource readImage( ReadImageParams readImageParams )
        throws IOException;
}
