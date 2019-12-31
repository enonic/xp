package com.enonic.xp.web.jetty.impl;

import org.eclipse.jetty.util.thread.ThreadPool;
import org.junit.jupiter.api.Test;

import com.enonic.xp.status.JsonStatusReporterTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpThreadPoolStatusReporterTest
    extends JsonStatusReporterTest
{
    @Test
    public void getName()
        throws Exception
    {
        final HttpThreadPoolStatusReporter reporter = new HttpThreadPoolStatusReporter( new ThreadPoolImpl( 8, 2, false ) );
        assertEquals( "http.threadpool", reporter.getName() );
    }

    @Test
    public void getReport()
        throws Exception
    {
        final HttpThreadPoolStatusReporter reporter = new HttpThreadPoolStatusReporter( new ThreadPoolImpl( 8, 2, false ) );
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
