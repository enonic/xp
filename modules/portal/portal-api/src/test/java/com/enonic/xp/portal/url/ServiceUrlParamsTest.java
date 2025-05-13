package com.enonic.xp.portal.url;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ServiceUrlParamsTest
{
    @Test
    public void testApplication()
    {
        final ServiceUrlParams params = new ServiceUrlParams();
        assertNull( params.getApplication() );

        params.application( "" );
        assertNull( params.getApplication() );

        params.application( "otherapplication" );
        assertEquals( "otherapplication", params.getApplication() );
    }

    @Test
    public void testService()
    {
        final ServiceUrlParams params = new ServiceUrlParams();
        assertNull( params.getService() );

        params.service( "" );
        assertNull( params.getService() );

        params.service( "myservice" );
        assertEquals( "myservice", params.getService() );
    }

    @Test
    public void testSetAsMap()
    {
        final ServiceUrlParams params = new ServiceUrlParams();
        params.service( "myservice" );
        params.application( "otherapplication" );
        params.param( "a", "1" );

        assertEquals( "myservice", params.getService() );
        assertEquals( "otherapplication", params.getApplication() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "ServiceUrlParams{type=server, params={a=[1]}, service=myservice, application=otherapplication}", params.toString() );
    }
}
