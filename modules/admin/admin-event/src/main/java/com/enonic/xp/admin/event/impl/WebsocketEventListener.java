package com.enonic.xp.admin.event.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.event.impl.json.EventJsonSerializer;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

@Component(immediate = true)
public final class WebsocketEventListener
    implements EventListener
{
    private final EventJsonSerializer serializer = new EventJsonSerializer();

    private final WebsocketManager websocketManager;

    @Activate
    public WebsocketEventListener( @Reference final WebsocketManager websocketManager )
    {
        this.websocketManager = websocketManager;
    }

    @Override
    public void onEvent( final Event event )
    {
        this.websocketManager.sendToAll( serializer.toJson( event ) );
    }
}
