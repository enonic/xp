package com.enonic.xp.portal.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class ServiceUrlParamsTest
    extends AbstractUrlParamsTest
{
    @Test
    public void testApplication()
    {
        final ServiceUrlParams params = configure( new ServiceUrlParams() );
        assertNull( params.getApplication() );

        params.application( "" );
        assertNull( params.getApplication() );

        params.application( "otherapplication" );
        assertEquals( "otherapplication", params.getApplication() );
    }

    @Test
    public void testService()
    {
        final ServiceUrlParams params = configure( new ServiceUrlParams() );
        assertNull( params.getService() );

        params.service( "" );
        assertNull( params.getService() );

        params.service( "myservice" );
        assertEquals( "myservice", params.getService() );
    }

    @Test
    public void testSetAsMap()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_service", "myservice" );
        map.put( "_application", "otherapplication" );
        map.put( "a", "1" );

        final ServiceUrlParams params = configure( new ServiceUrlParams() );
        params.setAsMap( map );

        assertEquals( "myservice", params.getService() );
        assertEquals( "otherapplication", params.getApplication() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "ServiceUrlParams{type=server, params={a=[1]}, service=myservice, application=otherapplication}", params.toString() );
    }
}
