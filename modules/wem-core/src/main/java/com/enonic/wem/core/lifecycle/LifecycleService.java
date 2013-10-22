package com.enonic.wem.core.lifecycle;

import com.google.inject.ImplementedBy;

@ImplementedBy(LifecycleServiceImpl.class)
public interface LifecycleService
{
    public void startAll()
        throws Exception;

    public void stopAll();
}
