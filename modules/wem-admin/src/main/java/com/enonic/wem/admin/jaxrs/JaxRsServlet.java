package com.enonic.wem.admin.jaxrs;

import java.util.Map;

import javax.servlet.ServletException;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

public abstract class JaxRsServlet
    extends ServletContainer
{
    private final JaxRsResourceConfig config;

    private Injector injector;

    public JaxRsServlet()
    {
        this.config = new JaxRsResourceConfig();
    }

    @Override
    protected ResourceConfig getDefaultResourceConfig( final Map<String, Object> props, final WebConfig wc )
        throws ServletException
    {
        this.config.getProperties().putAll( props );
        return this.config;
    }

    @Override
    protected void initiate( final ResourceConfig rc, final WebApplication wa )
    {
        configure();
        wa.initiate( rc, new JaxRsGuiceBridge( this.injector ) );
    }

    protected final void addClass( final Class<?> type )
    {
        this.config.addClass( type );
    }

    protected final void addSingleton( final Class<?> type )
    {
        addSingleton( this.injector.getInstance( type ) );
    }

    protected final void addSingleton( final Object type )
    {
        this.config.addSingleton( type );
    }

    protected final void setFeature( final String name, final boolean flag )
    {
        this.config.setFeature( name, flag );
    }

    protected final void setProperty( final String name, final Object value )
    {
        this.config.setProperty( name, value );
    }

    @Inject
    public final void setInjector( final Injector injector )
    {
        this.injector = injector;
    }

    protected abstract void configure();
}

