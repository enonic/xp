package com.enonic.xp.portal.url;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ComponentUrlParamsTest
{
    @Test
    void testId()
    {
        final ComponentUrlParams params = new ComponentUrlParams();
        assertNull( params.getId() );

        params.id( "" );
        assertNull( params.getId() );

        params.id( "123456" );
        assertEquals( "123456", params.getId() );
    }

    @Test
    void testPath()
    {
        final ComponentUrlParams params = new ComponentUrlParams();
        assertNull( params.getPath() );

        params.path( "" );
        assertNull( params.getPath() );

        params.path( "/a/b" );
        assertEquals( "/a/b", params.getPath() );
    }

    @Test
    void testComponent()
    {
        final ComponentUrlParams params = new ComponentUrlParams();
        assertNull( params.getComponent() );

        params.component( "" );
        assertNull( params.getComponent() );

        params.component( "main/0" );
        assertEquals( "main/0", params.getComponent() );
    }

    @Test
    void testSetAsMap()
    {
        final ComponentUrlParams params = new ComponentUrlParams();
        params.id( "123456" );
        params.path( "/a/b" );
        params.component( "main/0" );
        params.param( "a", "1" );

        assertEquals( "123456", params.getId() );
        assertEquals( "/a/b", params.getPath() );
        assertEquals( "main/0", params.getComponent() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "ComponentUrlParams{type=server, params={a=[1]}, id=123456, path=/a/b, component=main/0}", params.toString() );
    }
}

