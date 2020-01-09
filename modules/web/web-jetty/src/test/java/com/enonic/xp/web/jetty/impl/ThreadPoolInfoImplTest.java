package com.enonic.xp.web.jetty.impl;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThreadPoolInfoImplTest
{

    @Test
    public void getThreadPoolDetails()
    {
        final Server server = mock( Server.class );
        when( server.getThreadPool() ).thenReturn( new ThreadPool()
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
        final ThreadPoolInfoImpl threadPoolInfo = new ThreadPoolInfoImpl( server );

        assertEquals( 1, threadPoolInfo.getThreads() );
        assertEquals( 2, threadPoolInfo.getIdleThreads() );
        assertTrue( threadPoolInfo.isLowOnThreads() );
    }
}
