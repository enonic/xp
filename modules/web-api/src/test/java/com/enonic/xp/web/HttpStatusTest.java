package com.enonic.xp.web;

import org.junit.Test;

import static org.junit.Assert.*;

public class HttpStatusTest
{
    @Test
    public void testEnums()
    {
        testEnum( HttpStatus.OK, 200, "OK" );
    }

    private void testEnum( final HttpStatus status, final int value, final String reason )
    {
        assertEquals( value, status.value() );
        assertEquals( reason, status.getReasonPhrase() );
        assertEquals( String.valueOf( value ), status.toString() );
    }
}
