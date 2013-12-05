package com.enonic.wem.web.jaxrs;

import com.google.inject.Injector;
import com.sun.jersey.core.spi.component.ioc.IoCProxiedComponentProvider;

final class JaxRsGuiceInjectedProvider
    implements IoCProxiedComponentProvider
{
    private final Injector injector;

    public JaxRsGuiceInjectedProvider( final Injector injector )
    {
        this.injector = injector;
    }

    @Override
    public Object getInstance()
    {
        throw new IllegalStateException();
    }

    @Override
    public Object proxy( final Object o )
    {
        this.injector.injectMembers( o );
        return o;
    }
}
