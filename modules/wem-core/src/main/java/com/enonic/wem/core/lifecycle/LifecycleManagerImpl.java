package com.enonic.wem.core.lifecycle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
final class LifecycleManagerImpl
    implements LifecycleManager
{
    private final DisposableHandler handler;

    @Inject
    public LifecycleManagerImpl( final DisposableHandler handler )
    {
        this.handler = handler;
    }

    @Override
    public void dispose()
    {
        this.handler.disposeAll();
    }
}
