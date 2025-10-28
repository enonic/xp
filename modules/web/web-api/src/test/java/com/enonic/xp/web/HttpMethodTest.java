package com.enonic.xp.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpMethodTest
{
    @Test
    void testMethods()
    {
        assertEquals( 16, HttpMethod.values().length );
    }
}
