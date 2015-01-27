package com.enonic.xp.portal.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class AssetUrlParamsTest
    extends AbstractUrlParamsTest
{
    @Test
    public void testModule()
    {
        final AssetUrlParams params = configure( new AssetUrlParams() );
        assertNull( params.getModule() );

        params.module( "" );
        assertNull( params.getModule() );

        params.module( "othermodule" );
        assertEquals( "othermodule", params.getModule() );
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
        map.put( "_module", "othermodule" );
        map.put( "a", "1" );

        final AssetUrlParams params = configure( new AssetUrlParams() );
        params.setAsMap( map );

        assertEquals( "/a/b", params.getPath() );
        assertEquals( "othermodule", params.getModule() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "AssetUrlParams{params={a=[1]}, path=/a/b, module=othermodule}", params.toString() );
    }
}
