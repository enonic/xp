package com.enonic.wem.core.initializer;

import com.google.inject.AbstractModule;

public final class InitializerModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( StartupInitializer.class ).asEagerSingleton();
    }
}
