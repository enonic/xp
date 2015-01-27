package com.enonic.xp.portal.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class PageUrlParamsTest
    extends AbstractUrlParamsTest
{
    @Test
    public void testId()
    {
        final PageUrlParams params = configure( new PageUrlParams() );
        assertNull( params.getId() );

        params.id( "" );
        assertNull( params.getId() );

        params.id( "123456" );
        assertEquals( "123456", params.getId() );
    }

    @Test
    public void testPath()
    {
        final PageUrlParams params = configure( new PageUrlParams() );
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
        map.put( "_id", "123456" );
        map.put( "_path", "/a/b" );
        map.put( "a", "1" );

        final PageUrlParams params = configure( new PageUrlParams() );
        params.setAsMap( map );

        assertEquals( "123456", params.getId() );
        assertEquals( "/a/b", params.getPath() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "PageUrlParams{params={a=[1]}, id=123456, path=/a/b}", params.toString() );
    }
}
