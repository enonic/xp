package com.enonic.wem.guice.binder;

import com.google.inject.AbstractModule;

public abstract class OsgiModule
    extends AbstractModule
{
    protected final <T> ServiceBuilder<T> service( final Class<T> type )
    {
        return new ServiceBuilderImpl<>( binder(), type );
    }
}
