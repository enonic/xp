package com.enonic.wem.guice;

import com.google.inject.AbstractModule;

import com.enonic.wem.guice.internal.config.ConfigModule;
import com.enonic.wem.guice.internal.service.ServiceBinder;

public abstract class OsgiModule
    extends AbstractModule
{
    protected final <T> ServiceBuilder<T> service( final Class<T> type )
    {
        return ServiceBinder.service( binder(), type );
    }

    protected final void configPid( final String pid )
    {
        install( ConfigModule.forPid( pid ) );
    }
}
