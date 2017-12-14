package com.enonic.xp.web.thread;

public interface ThreadPoolInfo
{
    int getThreads();

    int getIdleThreads();

    boolean isLowOnThreads();
}
