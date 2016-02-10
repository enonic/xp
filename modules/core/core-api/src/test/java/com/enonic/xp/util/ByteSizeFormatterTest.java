package com.enonic.xp.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteSizeFormatterTest
{
    private final long factor = 1024L;


    @Test(expected = IllegalArgumentException.class)
    public void invalid()
        throws Exception
    {
        assertTrue( ByteSizeFormatter.parse( "1xp" ) == 1 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalid2()
        throws Exception
    {
        assertTrue( ByteSizeFormatter.parse( "mb" ) == 1 );
    }

    @Test
    public void bytes()
        throws Exception
    {
        assertTrue( ByteSizeFormatter.parse( "1b" ) == 1 );
        assertTrue( ByteSizeFormatter.parse( "2053b" ) == 2053 );
        assertTrue( ByteSizeFormatter.parse( "0kb" ) == 0 );
    }

    @Test
    public void kilobytes()
        throws Exception
    {
        assertTrue( ByteSizeFormatter.parse( "1kb" ) == factor );
        assertTrue( ByteSizeFormatter.parse( "2kb" ) == 2 * factor );
        assertTrue( ByteSizeFormatter.parse( "0kb" ) == 0 );
    }

    @Test
    public void megabytes()
        throws Exception
    {
        assertTrue( ByteSizeFormatter.parse( "1mb" ) == factor * factor );
        assertTrue( ByteSizeFormatter.parse( "1m" ) == factor * factor );
        assertTrue( ByteSizeFormatter.parse( "2mb" ) == 2 * factor * factor );
        assertTrue( ByteSizeFormatter.parse( "0mb" ) == 0 );
    }

    @Test
    public void gigabytes()
        throws Exception
    {
        assertTrue( ByteSizeFormatter.parse( "1gb" ) == factor * factor * factor );
        assertTrue( ByteSizeFormatter.parse( "1g" ) == factor * factor * factor );
        assertTrue( ByteSizeFormatter.parse( "2gb" ) == 2 * factor * factor * factor );
        assertTrue( ByteSizeFormatter.parse( "0gb" ) == 0 );
    }

    @Test
    public void terrabytes()
        throws Exception
    {
        assertTrue( ByteSizeFormatter.parse( "1tb" ) == factor * factor * factor * factor );
        assertTrue( ByteSizeFormatter.parse( "1t" ) == factor * factor * factor * factor );
        assertTrue( ByteSizeFormatter.parse( "2tb" ) == 2L * factor * factor * factor * factor );
        assertTrue( ByteSizeFormatter.parse( "0tb" ) == 0 );
    }
}