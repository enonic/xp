package com.enonic.wem.core.lifecycle;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public final class LifecycleModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final DisposableHandler disposableHandler = new DisposableHandler();
        bind( DisposableHandler.class ).toInstance( disposableHandler );
        bind( LifecycleManager.class ).to( LifecycleManagerImpl.class );
        bindListener( Matchers.any(), new InitializingHandler(), disposableHandler );
    }
}
