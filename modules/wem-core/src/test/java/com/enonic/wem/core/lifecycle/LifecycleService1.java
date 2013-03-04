package com.enonic.wem.core.lifecycle;

import com.google.inject.Singleton;

@Singleton
public class LifecycleService1
    implements InitializingBean, DisposableBean
{
    public boolean initialized = false;

    public boolean disposed = false;

    @Override
    public void afterPropertiesSet()
    {
        this.initialized = true;
    }

    @Override
    public void destroy()
    {
        this.disposed = true;
    }
}
