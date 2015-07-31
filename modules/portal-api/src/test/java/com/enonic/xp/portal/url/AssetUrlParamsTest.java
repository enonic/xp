package com.enonic.xp.portal.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class AssetUrlParamsTest
    extends AbstractUrlParamsTest
{
    @Test
    public void testApplication()
    {
        final AssetUrlParams params = configure( new AssetUrlParams() );
        assertNull( params.getApplication() );

        params.application( "" );
        assertNull( params.getApplication() );

        params.application( "otherapplication" );
        assertEquals( "otherapplication", params.getApplication() );
    }

    @Test
    public void testPath()
    {
        final AssetUrlParams params = configure( new AssetUrlParams() );
        assertNull( params.getPath() );

        params.path( "" );
        assertNull( params.getPath() );

        params.path( "/a/b" );
        assertEquals( "/a/b", params.getPath() );
    }

    @Test
    public void testSetAsMap()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_path", "/a/b" );
        map.put( "_application", "otherapplication" );
        map.put( "a", "1" );

        final AssetUrlParams params = configure( new AssetUrlParams() );
        params.setAsMap( map );

        assertEquals( "/a/b", params.getPath() );
        assertEquals( "otherapplication", params.getApplication() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "AssetUrlParams{params={a=[1]}, path=/a/b, application=otherapplication}", params.toString() );
    }
}
