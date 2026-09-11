package com.enonic.xp.image;

import java.io.IOException;

import com.google.common.io.ByteSource;

import com.enonic.xp.style.ImageStyle;

public interface ImageService
{
    /**
     * Resolves a predefined processing style by its application:name key.
     * Throws IllegalArgumentException for an unknown or incomplete processing style.
     */
    ImageStyle getStyle( String key );

    ByteSource readImage( ReadImageParams readImageParams )
        throws IOException;
}
