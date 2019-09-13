package com.enonic.xp.image;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ImageHelperTest
{
    @Test
    public void createImagePlaceholder()
    {
        final String str = ImageHelper.createImagePlaceholder( 2, 2 );
        assertNotNull( str );
        assertEquals( "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAC0lEQVR42mNgQAcAABIAAeRVjecAAAAASUVORK5CYII=",
                      str );
    }
}
