package com.enonic.xp.portal.impl.websocket;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.app.Application;
import com.enonic.xp.portal.websocket.WebSocketManager;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketContextFactory;
import com.enonic.xp.web.websocket.WebSocketService;

@Component(service = {WebSocketManager.class, WebSocketContextFactory.class})
public final class WebSocketManagerImpl
    implements WebSocketContextFactory, WebSocketManager, ServiceTrackerCustomizer<Application, Application>
{
    private final WebSocketRegistryImpl registry;

    private final WebSocketService webSocketService;

    private final BundleContext context;

    private final ServiceTracker<Application, Application> tracker;

    @Activate
    public WebSocketManagerImpl( final BundleContext context, @Reference final WebSocketService webSocketService )
    {
        this.webSocketService = webSocketService;
        this.registry = new WebSocketRegistryImpl();
        this.context = context;
        this.tracker = new ServiceTracker<>( context, Application.class, this );
        this.tracker.open();
    }

    @Deactivate
    public void deactivate()
    {
        this.tracker.close();
    }

    @Override
    public @Nullable Application addingService( final ServiceReference<Application> reference )
    {
        return this.context.getService( reference );
    }

    @Override
    public void modifiedService( final ServiceReference<Application> reference, final Application application )
    {
    }

    @Override
    public void removedService( final ServiceReference<Application> reference, final Application application )
    {
        // the application stopped or is being redeployed: its connections dispatch to a script
        // context of the gone incarnation — close them, so clients reconnect to the successor
        final List<WebSocketEntry> entries = this.registry.getByApplication( application.getKey() ).toList();
        entries.forEach( WebSocketEntry::close );
        this.context.ungetService( reference );
    }

    @Override
    public WebSocketContext newContext( final HttpServletRequest req, final HttpServletResponse res )
    {
        if ( !this.webSocketService.isUpgradeRequest( req ) )
        {
            return null;
        }

        final WebSocketContextImpl context = new WebSocketContextImpl();
        context.webSocketService = this.webSocketService;
        context.request = req;
        context.response = res;
        context.registry = this.registry;
        return context;
    }

    @Override
    public void send( final String id, final String message )
    {
        final WebSocketEntry entry = this.registry.getById( id );
        if ( entry != null )
        {
            entry.sendMessage( message );
        }
    }

    @Override
    public void sendToGroup( final String group, final String message )
    {
        this.registry.getByGroup( group ).forEach( e -> e.sendMessage( message ) );
    }

    @Override
    public int getGroupSize( final String group )
    {
        return Math.toIntExact( this.registry.getByGroup( group ).count() );
    }

    @Override
    public void addToGroup( final String group, final String id )
    {
        final WebSocketEntry entry = this.registry.getById( id );
        if ( entry != null )
        {
            entry.addGroup( group );
        }
    }

    @Override
    public void removeFromGroup( final String group, final String id )
    {
        final WebSocketEntry entry = this.registry.getById( id );
        if ( entry != null )
        {
            entry.removeGroup( group );
        }
    }
}
