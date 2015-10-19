package com.enonic.xp.web.jetty.impl.websocket;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import com.enonic.xp.web.jetty.impl.JettyController;
import com.enonic.xp.web.websocket.WebSocketHandler;

@Component(immediate = true)
public final class WebSocketManager
{
    private BundleContext bundleContext;

    private final Map<WebSocketHandler, WebSocketRegistration> handlers;

    private JettyController controller;

    public WebSocketManager()
    {
        this.handlers = Maps.newConcurrentMap();
    }

    @Activate
    public void activate( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
        this.handlers.values().forEach( this::registerIfPossible );
    }

    @Deactivate
    public void deactivate()
    {
        this.bundleContext = null;
        this.handlers.values().forEach( WebSocketRegistration::unregister );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addHandler( final WebSocketHandler handler )
    {
        final WebSocketRegistration reg = new WebSocketRegistration( handler );
        this.handlers.put( handler, reg );
        registerIfPossible( reg );
    }

    public void removeHandler( final WebSocketHandler handler )
    {
        final WebSocketRegistration reg = this.handlers.remove( handler );
        if ( reg != null )
        {
            reg.unregister();
        }
    }

    private void registerIfPossible( final WebSocketRegistration reg )
    {
        if ( this.bundleContext != null )
        {
            reg.register( this.bundleContext, this.controller.getServletContext() );
        }
    }

    @Reference
    public void setController( final JettyController controller )
    {
        this.controller = controller;
    }
}
