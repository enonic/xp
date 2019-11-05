package com.enonic.xp.web.session.impl.ignite.reporter;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.web.session.impl.AbstractSessionDataStoreFactoryActivator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebSessionReporterTest
{
    private IgniteCache<Object, Object> cache;

    private CacheMetrics cacheMetrics;

    private Ignite ignite;


    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.ignite = Mockito.mock( Ignite.class );
        this.cache = (IgniteCache<Object, Object>) Mockito.mock( IgniteCache.class );
        this.cacheMetrics = Mockito.mock( CacheMetrics.class );

        Mockito.when( ignite.cache( AbstractSessionDataStoreFactoryActivator.WEB_SESSION_CACHE ) ).thenReturn( cache );
        Mockito.when( this.cache.metrics() ).thenReturn( this.cacheMetrics );
        Mockito.when( this.cache.size() ).thenReturn( 123 );
        Mockito.when( this.cache.getName() ).thenReturn( AbstractSessionDataStoreFactoryActivator.WEB_SESSION_CACHE );
    }

    @Test
    public void with_metrics()
        throws Exception
    {
        Mockito.when( this.cacheMetrics.isStatisticsEnabled() ).thenReturn( true );
        Mockito.when( this.cacheMetrics.getValueType() ).thenReturn( Object.class.getName() );

        final WebSessionReporter reporter = new WebSessionReporter( this.ignite );

        assertEquals( reporter.getName(), "cache." + AbstractSessionDataStoreFactoryActivator.WEB_SESSION_CACHE );

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
