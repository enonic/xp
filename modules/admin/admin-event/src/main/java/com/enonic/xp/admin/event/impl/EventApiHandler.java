package com.enonic.xp.admin.event.impl;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.websocket.EndpointFactory;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketService;

@Component(immediate = true, service = UniversalApiHandler.class, property = {"applicationKey=admin", "apiKey=event",
    "displayName=Event API", "allowedPrincipals=role:system.admin.login", "mount=true"})
public class EventApiHandler
    implements UniversalApiHandler
{
    private final WebSocketService webSocketService;

    private final EndpointFactory endpointFactory;

    @Activate
    public EventApiHandler( @Reference final WebSocketService webSocketService,
                            @Reference/*(service = EventEndpointFactory.class)*/ final EndpointFactory endpointFactory )
    {
        this.webSocketService = webSocketService;
        this.endpointFactory = endpointFactory;
    }

    @Override
    public WebResponse handle( final WebRequest request )
    {
        final WebResponse.Builder<?> responseBuilder = WebResponse.create();

        if ( !webSocketService.isUpgradeRequest( request.getRawRequest(), null ) )
        {
            responseBuilder.status( HttpStatus.FORBIDDEN );
            return responseBuilder.build();
        }

        final WebSocketConfig webSocketConfig = new WebSocketConfig();
        webSocketConfig.setSubProtocols( List.of( "text" ) );

        responseBuilder.webSocket( webSocketConfig );

        return responseBuilder.build();
    }

    @Override
    public EndpointFactory getEndpointFactory()
    {
        return endpointFactory;
    }
}
