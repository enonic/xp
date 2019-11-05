package com.enonic.xp.web.jetty.impl;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.thread.ThreadPoolInfo;

@Component(immediate = true)
public class ThreadPoolInfoImpl
    implements ThreadPoolInfo
{
    private final ThreadPool threadPool;

    @Activate
    public ThreadPoolInfoImpl( @Reference final Server server )
    {
        this.threadPool = server.getThreadPool();
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
