package com.enonic.wem.core.initializer;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class InitializerModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( StartupInitializer.class ).in( Scopes.SINGLETON );
    }
}
