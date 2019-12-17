package com.enonic.xp.admin.impl.market;

import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarketRequestFactoryTest
{
    @Test
    void test_create()
    {
        final List<String> ids = Arrays.asList( "1", "2", "3" );
        final String url = "https://market.test.com/test";
        final String version = "newest";
        final int from = 0;
        final int to = 10;

        final HttpRequest request = MarketRequestFactory.
            create( url, ids, version, from, to );

        assertEquals( "https", request.uri().getScheme() );

        assertEquals( request.uri().getPath(), "/test" );
        assertTrue( request.uri().getQuery().contains( "xpVersion=newest" ) );
        assertTrue( request.uri().getQuery().contains( "start=0" ) );
        assertTrue( request.uri().getQuery().contains( "count=10" ) );
    }
}
