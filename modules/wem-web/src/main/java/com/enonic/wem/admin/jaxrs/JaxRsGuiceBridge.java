package com.enonic.wem.admin.jaxrs;

import com.google.inject.Injector;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

final class JaxRsGuiceBridge
    implements IoCComponentProviderFactory
{
    private final Injector injector;

    public JaxRsGuiceBridge( final Injector injector )
    {
        this.injector = injector;
    }

    @Override
    public IoCComponentProvider getComponentProvider( final Class<?> clz )
    {
        return getComponentProvider( null, clz );
    }

    @Override
    public IoCComponentProvider getComponentProvider( final ComponentContext context, final Class<?> clz )
    {
        return new JaxRsGuiceInjectedProvider( this.injector );
    }
}
