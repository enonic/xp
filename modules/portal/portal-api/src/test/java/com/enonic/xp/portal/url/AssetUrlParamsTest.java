package com.enonic.xp.portal.url;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AssetUrlParamsTest
{
    @Test
    public void testApplication()
    {
        final AssetUrlParams params = new AssetUrlParams();
        assertNull( params.getApplication() );

        params.application( "" );
        assertNull( params.getApplication() );

        params.application( "otherapplication" );
        assertEquals( "otherapplication", params.getApplication() );
    }

    @Test
    public void testPath()
    {
        final AssetUrlParams params = new AssetUrlParams();
        assertNull( params.getPath() );

        params.path( "" );
        assertNull( params.getPath() );

        params.path( "/a/b" );
        assertEquals( "/a/b", params.getPath() );
    }

    @Test
    public void testSetAsMap()
    {
        final AssetUrlParams params = new AssetUrlParams();
        params.path( "/a/b" );
        params.application( "otherapplication" );
        params.param( "a", "1" );

        assertEquals( "/a/b", params.getPath() );
        assertEquals( "otherapplication", params.getApplication() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "AssetUrlParams{type=server, params={a=[1]}, path=/a/b, application=otherapplication}", params.toString() );
    }
}
