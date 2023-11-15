package com.enonic.xp.image;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageHelperTest
{
    @Test
    void createImagePlaceholder()
    {
        final String str = ImageHelper.createImagePlaceholder( 2, 3 );
        assertEquals( "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAADCAYAAAC56t6BAAAAC0lEQVR4nGNgwAcAAB4AAfb96ZYAAAAASUVORK5CYII=",
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
