package com.enonic.xp.web.jetty.impl;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.junit.jupiter.api.Test;

import com.enonic.xp.status.JsonStatusReporterTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpThreadPoolStatusReporterTest
    extends JsonStatusReporterTest
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
