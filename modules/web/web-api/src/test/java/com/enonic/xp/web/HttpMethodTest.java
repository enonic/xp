package com.enonic.xp.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HttpMethodTest
{
    @Test
    public void testMethods()
    {
        assertEquals( 16, HttpMethod.values().length );
    }
}
