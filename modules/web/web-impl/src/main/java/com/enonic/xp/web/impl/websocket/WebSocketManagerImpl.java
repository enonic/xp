package com.enonic.xp.web.impl.websocket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketManager;
import com.enonic.xp.web.websocket.WebSocketService;

@Component(service = {WebSocketManager.class, WebSocketContextFactory.class})
public final class WebSocketManagerImpl
    implements WebSocketContextFactory, WebSocketManager
{
    private final WebSocketRegistryImpl registry;

    private WebSocketService webSocketService;

    public WebSocketManagerImpl()
    {
        this.registry = new WebSocketRegistryImpl();
    }

    @Override
    public WebSocketContext newContext( final HttpServletRequest req, final HttpServletResponse res )
    {
        if ( !this.webSocketService.isUpgradeRequest( req, res ) )
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

    @Reference
    public void setWebSocketService( final WebSocketService webSocketService )
    {
        this.webSocketService = webSocketService;
    }
}
