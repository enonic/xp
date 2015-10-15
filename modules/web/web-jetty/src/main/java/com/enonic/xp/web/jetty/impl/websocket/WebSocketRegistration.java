package com.enonic.xp.web.jetty.impl.websocket;

import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.web.websocket.WebSocketHandler;

final class WebSocketRegistration
{
    private final WebSocketHandler handler;

    private final WebSocketServlet servlet;

    private ServiceRegistration<?> registration;

    public WebSocketRegistration( final WebSocketHandler handler )
    {
        this.handler = handler;
        this.servlet = new WebSocketServlet( handler );
    }

    public void register( final BundleContext context, final ServletContext realContext )
    {
        if ( this.registration != null )
        {
            return;
        }

        final Hashtable<String, Object> config = new Hashtable<>();
        config.put( "osgi.http.whiteboard.servlet.pattern", this.handler.getPath() );

        this.servlet.realContext = realContext;
        this.registration = context.registerService( Servlet.class, this.servlet, config );
    }

    public void unregister()
    {
        if ( this.registration == null )
        {
            return;
        }

        this.registration.unregister();
        this.registration = null;
    }
}
