package com.enonic.xp.web.jetty.impl;

import java.io.ByteArrayOutputStream;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.support.JsonTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpThreadPoolStatusReporterTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @Test
    void getName()
    {
        final Server server = mock( Server.class );
        when( server.getThreadPool() ).thenReturn( new ThreadPoolImpl( 8, 2, false ) );
        final HttpThreadPoolStatusReporter reporter = new HttpThreadPoolStatusReporter( server );
        assertEquals( "http.threadpool", reporter.getName() );
    }

    @Test
    void getReport()
        throws Exception
    {
        final Server server = mock( Server.class );

        when( server.getThreadPool() ).thenReturn( new ThreadPoolImpl( 8, 2, false ) );
        final HttpThreadPoolStatusReporter reporter = new HttpThreadPoolStatusReporter( server );
        assertJson( "http_thread_pool_report.json" , reporter );
    }

    private void assertJson( final String fileName, final StatusReporter reporter )
        throws Exception
    {
        assertEquals( MediaType.JSON_UTF_8, reporter.getMediaType() );

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reporter.report( outputStream );

        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( fileName ),
                                         jsonTestHelper.bytesToJson( outputStream.toByteArray() ) );
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
