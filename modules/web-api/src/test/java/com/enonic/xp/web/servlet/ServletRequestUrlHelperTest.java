package com.enonic.xp.web.servlet;

import org.junit.Test;

import static org.junit.Assert.*;

public class ServletRequestUrlHelperTest
{
    @Test
    public void createUri()
    {
        final String uri1 = ServletRequestUrlHelper.createUri( null );
        assertEquals( "", uri1 );

        final String uri2 = ServletRequestUrlHelper.createUri( "" );
        assertEquals( "", uri2 );

        final String uri3 = ServletRequestUrlHelper.createUri( "a/b" );
        assertEquals( "/a/b", uri3 );

        final String uri4 = ServletRequestUrlHelper.createUri( "/a/b" );
        assertEquals( "/a/b", uri4 );
    }
}
