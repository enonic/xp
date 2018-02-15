package com.enonic.xp.web.session.impl.reporter;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.web.session.impl.WebSessionFilter;

public class WebSessionReportTest
{
    private IgniteCache<Object, Object> cache;

    private CacheMetrics cacheMetrics;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp()
        throws Exception
    {
        this.cache = (IgniteCache<Object, Object>) Mockito.mock( IgniteCache.class );
        this.cacheMetrics = Mockito.mock( CacheMetrics.class );
        Mockito.when( this.cache.metrics() ).thenReturn( this.cacheMetrics );
        Mockito.when( this.cache.size() ).thenReturn( 123 );

        Mockito.when( this.cache.getName() ).thenReturn( WebSessionFilter.WEB_SESSION_CACHE );
    }

    @Test
    public void with_metrics()
        throws Exception
    {
        Mockito.when( this.cacheMetrics.isStatisticsEnabled() ).thenReturn( true );
        Mockito.when( this.cacheMetrics.getValueType() ).thenReturn( Object.class.getName() );

        final JsonNode result = WebSessionReport.create().
            cache( this.cache ).
            build().
            toJson();

        assertJson( "metrics.json", result );
    }

    @Test
    public void no_metrics()
        throws Exception
    {
        Mockito.when( this.cacheMetrics.isStatisticsEnabled() ).thenReturn( false );

        final JsonNode result = WebSessionReport.create().
            cache( this.cache ).
            build().
            toJson();

        assertJson( "no-metrics.json", result );
    }


    private void assertJson( final String fileName, final JsonNode json )
    {
        final JsonTestHelper jsonTestHelper = new JsonTestHelper( this );
        final JsonNode jsonFromFile = jsonTestHelper.loadTestJson( fileName );
        jsonTestHelper.assertJsonEquals( jsonFromFile, json );
    }
}