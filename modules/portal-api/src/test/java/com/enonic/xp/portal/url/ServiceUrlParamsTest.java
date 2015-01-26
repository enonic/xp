package com.enonic.xp.portal.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.module.ModuleKey;

import static org.junit.Assert.*;

public class ServiceUrlParamsTest
    extends AbstractUrlParamsTest
{
    @Test
    public void testModule()
    {
        final ServiceUrlParams params = configure( new ServiceUrlParams() );
        assertNull( params.getModule() );

        params.module( "" );
        assertNull( params.getModule() );

        params.module( "othermodule" );
        assertEquals( ModuleKey.from( "othermodule" ), params.getModule() );
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
        map.put( "_module", "othermodule" );
        map.put( "a", "1" );

        final ServiceUrlParams params = configure( new ServiceUrlParams() );
        params.setAsMap( map );

        assertEquals( "myservice", params.getService() );
        assertEquals( ModuleKey.from( "othermodule" ), params.getModule() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "ServiceUrlParams{params={a=[1]}, service=myservice, module=othermodule}", params.toString() );
    }
}
