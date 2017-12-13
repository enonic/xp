package com.enonic.xp.web.jetty.impl;

import org.eclipse.jetty.util.thread.ThreadPool;

import com.enonic.xp.web.thread.ThreadPoolInfo;

public class ThreadPoolInfoImpl
    implements ThreadPoolInfo
{
    private final ThreadPool threadPool;

    public ThreadPoolInfoImpl( final ThreadPool threadPool )
    {
        this.threadPool = threadPool;
    }

    @Override
    public int getThreads()
    {
        return threadPool.getThreads();
    }

    @Override
    public int getIdleThreads()
    {
        return threadPool.getIdleThreads();
    }

    @Override
    public boolean isLowOnThreads()
    {
        return threadPool.isLowOnThreads();
    }
}
