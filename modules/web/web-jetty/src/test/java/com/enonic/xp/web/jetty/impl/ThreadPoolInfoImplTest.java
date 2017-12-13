package com.enonic.xp.web.jetty.impl;

import org.eclipse.jetty.util.thread.ThreadPool;
import org.junit.Test;

import static org.junit.Assert.*;

public class ThreadPoolInfoImplTest
{

    @Test
    public void getThreadPoolDetails()
    {
        final ThreadPoolInfoImpl threadPoolInfo = new ThreadPoolInfoImpl( new ThreadPool()
        {
            @Override
            public void join()
                throws InterruptedException
            {
            }

            @Override
            public int getThreads()
            {
                return 1;
            }

            @Override
            public int getIdleThreads()
            {
                return 2;
            }

            @Override
            public boolean isLowOnThreads()
            {
                return true;
            }

            @Override
            public void execute( final Runnable command )
            {

            }
        } );

        assertEquals( 1, threadPoolInfo.getThreads() );
        assertEquals( 2, threadPoolInfo.getIdleThreads() );
        assertEquals( true, threadPoolInfo.isLowOnThreads() );
    }
}