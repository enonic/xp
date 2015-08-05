package com.enonic.xp.image;

import java.awt.image.BufferedImage;

import com.google.common.io.ByteSource;

public interface ImageService
{
    BufferedImage readImage( final ByteSource blob, final ReadImageParams readImageParams );
}
