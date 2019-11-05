package com.enonic.xp.web.jetty.impl;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.Resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpThreadPoolStatusReporterTest
{
    @Test
    public void getName()
        throws Exception
    {
        final Server server = mock( Server.class );
        when( server.getThreadPool() ).thenReturn( new ThreadPoolImpl( 8, 2, false ) );
        final HttpThreadPoolStatusReporter reporter = new HttpThreadPoolStatusReporter( server );
        assertEquals( "http.threadpool", reporter.getName() );
    }

    @Test
    public void getReport()
        throws Exception
    {
        final Server server = mock( Server.class );

        when( server.getThreadPool() ).thenReturn( new ThreadPoolImpl( 8, 2, false ) );
        final HttpThreadPoolStatusReporter reporter = new HttpThreadPoolStatusReporter( server );
        assertEquals( parseJson( readFromFile( "http_thread_pool_report.json" ) ), reporter.getReport() );
    }

    private String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }

        return Resources.toString( url, StandardCharsets.UTF_8 );
    }

    private JsonNode parseJson( final String json )
        throws Exception
    {
        final ObjectMapper mapper = createObjectMapper();
        return mapper.readTree( json );
    }

    private ObjectMapper createObjectMapper()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
        mapper.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
        mapper.setSerializationInclusion( JsonInclude.Include.ALWAYS );
        return mapper;
    }

    private static class ThreadPoolImpl
        implements ThreadPool
    {
        final int threads;

        final int idleThreads;

        final boolean lowOnThreads;

        ThreadPoolImpl( final int threads, final int idleThreads, final boolean lowOnThreads )
        {
            this.threads = threads;
            this.idleThreads = idleThreads;
            this.lowOnThreads = lowOnThreads;
        }

        @Override
        public void join()
            throws InterruptedException
        {
        }

        @Override
        public int getThreads()
        {
            return threads;
        }

        @Override
        public int getIdleThreads()
        {
            return idleThreads;
        }

        @Override
        public boolean isLowOnThreads()
        {
            return lowOnThreads;
        }

        @Override
        public void execute( final Runnable command )
        {
        }
    }
}
