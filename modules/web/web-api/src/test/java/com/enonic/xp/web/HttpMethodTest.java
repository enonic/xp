package com.enonic.xp.web;

import org.junit.Test;

import static org.junit.Assert.*;

public class HttpMethodTest
{
    @Test
    public void testMethods()
    {
        assertEquals( 7, HttpMethod.values().length );

        testMethod( HttpMethod.GET, "GET" );
        testMethod( HttpMethod.POST, "POST" );
        testMethod( HttpMethod.HEAD, "HEAD" );
        testMethod( HttpMethod.OPTIONS, "OPTIONS" );
        testMethod( HttpMethod.PUT, "PUT" );
        testMethod( HttpMethod.DELETE, "DELETE" );
        testMethod( HttpMethod.TRACE, "TRACE" );
    }

    private void testMethod( final HttpMethod method, final String name )
    {
        assertEquals( name, method.toString() );
    }
}
