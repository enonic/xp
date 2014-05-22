package com.enonic.wem.portal2.http;

import org.junit.Test;

import static org.junit.Assert.*;

public class HttpMethodTest
{
    @Test
    public void methodNames()
    {
        assertEquals( "GET", HttpMethod.GET.toString() );
        assertEquals( "POST", HttpMethod.POST.toString() );
        assertEquals( "DELETE", HttpMethod.DELETE.toString() );
        assertEquals( "HEAD", HttpMethod.HEAD.toString() );
        assertEquals( "OPTIONS", HttpMethod.OPTIONS.toString() );
        assertEquals( "PATCH", HttpMethod.PATCH.toString() );
        assertEquals( "TRACE", HttpMethod.TRACE.toString() );
        assertEquals( "PUT", HttpMethod.PUT.toString() );
    }
}
