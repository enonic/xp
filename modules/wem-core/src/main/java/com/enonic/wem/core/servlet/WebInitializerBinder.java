package com.enonic.wem.core.servlet;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public final class WebInitializerBinder
{
    private final Multibinder<WebInitializer> binder;

    private WebInitializerBinder( final Binder binder )
    {
        this.binder = Multibinder.newSetBinder( binder, WebInitializer.class );
    }

    public <T extends WebInitializer> void add( final Class<T> bean )
    {
        this.binder.addBinding().to( bean ).in( Scopes.SINGLETON );
    }

    public static WebInitializerBinder from( final Binder binder )
    {
        return new WebInitializerBinder( binder );
    }
}
