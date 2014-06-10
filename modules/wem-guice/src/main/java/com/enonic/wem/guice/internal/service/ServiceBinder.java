package com.enonic.wem.guice.internal.service;

import com.google.inject.Binder;

import com.enonic.wem.guice.ServiceBuilder;

public final class ServiceBinder
{
    public static <T> ServiceBuilder<T> service( final Binder binder, final Class<T> type )
    {
        return new ServiceBuilderImpl<>( binder, type );
    }
}
