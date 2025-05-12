package com.enonic.xp.image;

import java.io.IOException;

import com.google.common.io.ByteSource;

public interface ImageService
{
    ByteSource readImage( ReadImageParams readImageParams )
        throws IOException;
}
