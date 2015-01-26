package com.enonic.xp.portal.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class ComponentUrlParamsTest
    extends AbstractUrlParamsTest
{
    @Test
    public void testId()
    {
        final ComponentUrlParams params = configure( new ComponentUrlParams() );
        assertNull( params.getId() );

        params.id( "" );
        assertNull( params.getId() );

        params.id( "123456" );
        assertEquals( "123456", params.getId() );
    }

    @Test
    public void testPath()
    {
        final ComponentUrlParams params = configure( new ComponentUrlParams() );
        assertNull( params.getPath() );

        params.path( "" );
        assertNull( params.getPath() );

        params.path( "/a/b" );
        assertEquals( "/a/b", params.getPath() );
    }

    @Test
    public void testComponent()
    {
        final ComponentUrlParams params = configure( new ComponentUrlParams() );
        assertNull( params.getComponent() );

        params.component( "" );
        assertNull( params.getComponent() );

        params.component( "main/0" );
        assertEquals( "main/0", params.getComponent() );
    }

    @Test
    public void testSetAsMap()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_id", "123456" );
        map.put( "_path", "/a/b" );
        map.put( "_component", "main/0" );
        map.put( "a", "1" );

        final ComponentUrlParams params = configure( new ComponentUrlParams() );
        params.setAsMap( map );

        assertEquals( "123456", params.getId() );
        assertEquals( "/a/b", params.getPath() );
        assertEquals( "main/0", params.getComponent() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "ComponentUrlParams{params={a=[1]}, id=123456, path=/a/b, component=main/0}", params.toString() );
    }
}

