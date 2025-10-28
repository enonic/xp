package com.enonic.xp.core.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ByteSizeParserTest
{
    private final long factor = 1024L;


    @Test
    void invalid()
    {
        assertThrows(IllegalArgumentException.class, () -> assertTrue( ByteSizeParser.parse( "1xp" ) == 1 ));
    }

    @Test
    void invalid2()
    {
        assertThrows(IllegalArgumentException.class, () -> assertTrue( ByteSizeParser.parse( "mb" ) == 1 ));
    }

    @Test
    void bytes()
    {
        assertTrue( ByteSizeParser.parse( "1b" ) == 1 );
        assertTrue( ByteSizeParser.parse( "2053b" ) == 2053 );
        assertTrue( ByteSizeParser.parse( "0kb" ) == 0 );
    }

    @Test
    void kilobytes()
    {
        assertTrue( ByteSizeParser.parse( "1kb" ) == factor );
        assertTrue( ByteSizeParser.parse( "2kb" ) == 2 * factor );
        assertTrue( ByteSizeParser.parse( "0kb" ) == 0 );
    }

    @Test
    void megabytes()
    {
        assertTrue( ByteSizeParser.parse( "1mb" ) == factor * factor );
        assertTrue( ByteSizeParser.parse( "1m" ) == factor * factor );
        assertTrue( ByteSizeParser.parse( "2mb" ) == 2 * factor * factor );
        assertTrue( ByteSizeParser.parse( "0mb" ) == 0 );
    }

    @Test
    void gigabytes()
    {
        assertTrue( ByteSizeParser.parse( "1gb" ) == factor * factor * factor );
        assertTrue( ByteSizeParser.parse( "1g" ) == factor * factor * factor );
        assertTrue( ByteSizeParser.parse( "2gb" ) == 2 * factor * factor * factor );
        assertTrue( ByteSizeParser.parse( "0gb" ) == 0 );
    }

    @Test
    void terrabytes()
    {
        assertTrue( ByteSizeParser.parse( "1tb" ) == factor * factor * factor * factor );
        assertTrue( ByteSizeParser.parse( "1t" ) == factor * factor * factor * factor );
        assertTrue( ByteSizeParser.parse( "2tb" ) == 2L * factor * factor * factor * factor );
        assertTrue( ByteSizeParser.parse( "0tb" ) == 0 );
    }
}
