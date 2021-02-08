package com.enonic.xp.image;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ImageHelperTest
{
    @Test
    void createImagePlaceholder()
    {
        final String str = ImageHelper.createImagePlaceholder( 2, 2 );
        assertNotNull( str );
        assertEquals( "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAC0lEQVR42mNgQAcAABIAAeRVjecAAAAASUVORK5CYII=",
                      str );
    }

    @Test
    void getFormatByMimeType()
        throws Exception
    {
        assertEquals( "png", ImageHelper.getFormatByMimeType( "image/png" ) );
        assertEquals( "JPEG", ImageHelper.getFormatByMimeType( "image/jpeg" ) );
        assertEquals( "gif", ImageHelper.getFormatByMimeType( "image/gif" ) );
    }
}
