package com.enonic.xp.portal.url;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PageUrlParamsTest
{
    @Test
    public void testId()
    {
        final PageUrlParams params = new PageUrlParams();
        assertNull( params.getId() );

        params.id( "" );
        assertNull( params.getId() );

        params.id( "123456" );
        assertEquals( "123456", params.getId() );
    }

    @Test
    public void testPath()
    {
        final PageUrlParams params = new PageUrlParams();
        assertNull( params.getPath() );

        params.path( "" );
        assertNull( params.getPath() );

        params.path( "/a/b" );
        assertEquals( "/a/b", params.getPath() );
    }

    @Test
    public void testSetAsMap()
    {
        final PageUrlParams params = new PageUrlParams();
        params.path( "/a/b" );
        params.id( "123456" );
        params.param( "a", "1" );

        assertEquals( "123456", params.getId() );
        assertEquals( "/a/b", params.getPath() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "PageUrlParams{type=server, params={a=[1]}, id=123456, path=/a/b}", params.toString() );
    }
}
