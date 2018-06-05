package com.enonic.xp.web.session.impl.reporter;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.web.session.impl.IgniteSessionDataStore;

import static org.junit.Assert.*;

public class WebSessionReporterTest
{
    private IgniteCache<Object, Object> cache;

    private CacheMetrics cacheMetrics;

    private Ignite ignite;


    @SuppressWarnings("unchecked")
    @Before
    public void setUp()
        throws Exception
    {
        this.ignite = Mockito.mock( Ignite.class );
        this.cache = (IgniteCache<Object, Object>) Mockito.mock( IgniteCache.class );
        this.cacheMetrics = Mockito.mock( CacheMetrics.class );

        Mockito.when( this.ignite.cache( IgniteSessionDataStore.WEB_SESSION_CACHE ) ).thenReturn( this.cache );
        Mockito.when( this.cache.metrics() ).thenReturn( this.cacheMetrics );
        Mockito.when( this.cache.size() ).thenReturn( 123 );
        Mockito.when( this.cache.getName() ).thenReturn( IgniteSessionDataStore.WEB_SESSION_CACHE );
    }

    @Test
    public void with_metrics()
        throws Exception
    {
        Mockito.when( this.cacheMetrics.isStatisticsEnabled() ).thenReturn( true );
        Mockito.when( this.cacheMetrics.getValueType() ).thenReturn( Object.class.getName() );

        final WebSessionReporter reporter = new WebSessionReporter();
        reporter.setIgnite( this.ignite );

        assertEquals( reporter.getName(), "cache." + IgniteSessionDataStore.WEB_SESSION_CACHE );

        final JsonNode result = reporter.getReport();
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