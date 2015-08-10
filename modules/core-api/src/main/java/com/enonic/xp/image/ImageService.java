package com.enonic.xp.image;

import java.io.IOException;

import com.google.common.io.ByteSource;

public interface ImageService
{
    byte[] readImage( final ByteSource blob, final String id, final String binaryReference, final ReadImageParams readImageParams )
        throws IOException;

    String getFormatByMimeType( String mimeType )
        throws IOException;
}
