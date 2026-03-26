package com.enonic.xp.core.impl.image;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExternalImageProcessorTest
{
    @Test
    void supportsFormat()
    {
        final ExternalImageProcessor processor = new ExternalImageProcessor( "vipsthumbnail" );

        assertTrue( processor.supportsFormat( "png" ) );
        assertTrue( processor.supportsFormat( "jpeg" ) );
        assertTrue( processor.supportsFormat( "gif" ) );
        assertTrue( processor.supportsFormat( "webp" ) );

        assertFalse( processor.supportsFormat( "bmp" ) );
        assertFalse( processor.supportsFormat( "tiff" ) );
        assertFalse( processor.supportsFormat( "" ) );
    }
}
