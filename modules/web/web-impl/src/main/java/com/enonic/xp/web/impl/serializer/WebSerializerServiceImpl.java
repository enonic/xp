package com.enonic.xp.web.impl.serializer;

import java.io.IOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.serializer.WebSerializerService;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

@Component
public final class WebSerializerServiceImpl
    implements WebSerializerService
{
    private final WebSocketContextFactory webSocketContextFactory;

    @Activate
    public WebSerializerServiceImpl( @Reference final WebSocketContextFactory webSocketContextFactory )
    {
        this.webSocketContextFactory = webSocketContextFactory;
    }

    @Override
    public WebRequest request( final HttpServletRequest httpRequest, final HttpServletResponse httpResponse )
    {
        final WebRequest webRequest = new WebRequest();
        new RequestSerializer( webRequest ).serialize( httpRequest );
        if ( httpResponse != null )
        {
            final WebSocketContext webSocketContext = this.webSocketContextFactory.newContext( httpRequest, httpResponse );
            webRequest.setWebSocketContext( webSocketContext );
        }
        return webRequest;
    }

    @Override
    public void response( final WebRequest webRequest, final WebResponse webResponse, final HttpServletResponse response )
        throws IOException
    {
        new ResponseSerializer( webRequest, webResponse ).serialize( response );
    }
}
