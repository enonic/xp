package com.enonic.xp.image;

import java.io.IOException;

public interface ImageService
{
    byte[] readImage( final ReadImageParams readImageParams )
        throws IOException;

    String getFormatByMimeType( String mimeType )
        throws IOException;
}
