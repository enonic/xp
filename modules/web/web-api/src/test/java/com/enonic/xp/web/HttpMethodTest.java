package com.enonic.xp.web;

import org.junit.Test;

import static org.junit.Assert.*;

public class HttpMethodTest
{
    @Test
    public void testMethods()
    {
        assertEquals( 16, HttpMethod.values().length );
    }
}
