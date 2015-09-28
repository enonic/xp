package com.enonic.xp.image;

import java.io.IOException;

import com.google.common.io.ByteSource;

public interface ImageService
{
    ByteSource readImage( final ReadImageParams readImageParams )
        throws IOException;

    String getFormatByMimeType( String mimeType )
        throws IOException;
}
