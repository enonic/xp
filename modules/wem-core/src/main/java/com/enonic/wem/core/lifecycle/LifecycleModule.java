package com.enonic.wem.core.lifecycle;

import com.google.inject.AbstractModule;

public final class LifecycleModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( LifecycleService.class ).to( LifecycleServiceImpl.class );
    }
}
