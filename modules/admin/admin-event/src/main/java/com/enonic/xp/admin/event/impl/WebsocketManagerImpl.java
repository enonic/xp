package com.enonic.xp.admin.event.impl;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class WebsocketManagerImpl
    implements WebsocketManager
{
    private final CopyOnWriteArraySet<EventEndpoint> sockets = new CopyOnWriteArraySet<>();

    private final Executor executor;

    @Activate
    public WebsocketManagerImpl( @Reference(service = WebsocketEventExecutor.class) final Executor websocketEventExecutor )
    {
        this.executor = websocketEventExecutor;
    }

    @Override
    public void registerSocket( final EventEndpoint eventEndpoint )
    {
        this.sockets.add( eventEndpoint );
    }

    @Override
    public void unregisterSocket( final EventEndpoint eventEndpoint )
    {
        this.sockets.remove( eventEndpoint );
    }

    @Override
    public void sendToAll( final String message )
    {
        sockets.forEach( s -> executor.execute( () -> s.sendMessage( message ) ) );
    }
}
