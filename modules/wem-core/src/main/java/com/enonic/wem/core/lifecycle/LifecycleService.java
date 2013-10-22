package com.enonic.wem.core.lifecycle;

import com.google.inject.ImplementedBy;

@ImplementedBy(LifecycleServiceImpl.class)
public interface LifecycleService
{
    public void startAll();

    public void stopAll();
}
