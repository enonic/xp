package com.enonic.xp.image;

import java.io.IOException;

import com.google.common.io.ByteSource;

public interface ImageService
{
    byte[] readImage( final ByteSource blob, final ReadImageParams readImageParams )
        throws IOException;
}
