package com.enonic.xp.admin.impl.market;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.squareup.okhttp.Request;

import static org.junit.Assert.*;

public class MarketRequestFactoryTest
{
    @Test
    public void test_create()
        throws Exception
    {
        final List<String> ids = Arrays.asList( "1", "2", "3" );
        final String url = "https://market.test.com/test", version = "newest";
        final Integer from = 0, to = 10;

        final Request request = MarketRequestFactory.
            create( url, ids, version, from, to );

        assertTrue( request.isHttps() );

        assertEquals( request.httpUrl().encodedPath(), "/test" );
        assertEquals( request.httpUrl().queryParameter( "xpVersion" ), version );
        assertEquals( request.httpUrl().queryParameter( "start" ), from.toString() );
        assertEquals( request.httpUrl().queryParameter( "count" ), to.toString() );

    }
}