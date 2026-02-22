package com.enonic.xp.web.impl.handler;

import java.io.IOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.impl.serializer.RequestBodyReader;
import com.enonic.xp.web.serializer.WebSerializerService;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

@Component(immediate = true, service = Servlet.class, property = {"connector=xp"})
@Order(100)
@WebServlet("/*")
public final class WebDispatcherServlet
    extends HttpServlet
{
    private final WebDispatcher webDispatcher;

    private final ExceptionMapper exceptionMapper;

    private final ExceptionRenderer exceptionRenderer;

    private final WebSocketContextFactory webSocketContextFactory;

    private final WebSerializerService webSerializerService;

    @Activate
    public WebDispatcherServlet( @Reference final WebDispatcher webDispatcher, @Reference final ExceptionMapper exceptionMapper,
                                 @Reference final ExceptionRenderer exceptionRenderer,
                                 @Reference final WebSocketContextFactory webSocketContextFactory,
                                 @Reference final WebSerializerService webSerializerService )
    {
        this.webDispatcher = webDispatcher;
        this.exceptionMapper = exceptionMapper;
        this.exceptionRenderer = exceptionRenderer;
        this.webSocketContextFactory = webSocketContextFactory;
        this.webSerializerService = webSerializerService;
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final WebRequest webRequest = webSerializerService.request( req );
        final WebSocketContext webSocketContext = this.webSocketContextFactory.newContext( req, res );
        webRequest.setWebSocketContext( webSocketContext );
        webRequest.setBody( RequestBodyReader.readBody( req ) );

        final WebResponse webResponse = doHandle( webRequest );

        if ( webRequest.getWebSocketContext() != null && webResponse.getWebSocket() != null )
        {
            return;
        }

        webSerializerService.response( webRequest, webResponse, res );
    }

    private WebResponse doHandle( final WebRequest webRequest )
    {
        try
        {
            final WebResponse webResponse = webDispatcher.dispatch( webRequest, WebResponse.create().build() );
            if ( !Boolean.TRUE.equals( webRequest.getRawRequest().getAttribute( "error.handled" ) ) )
            {
                this.exceptionMapper.throwIfNeeded( webResponse );
            }
            return webResponse;
        }
        catch ( Exception e )
        {
            return exceptionRenderer.render( webRequest, e );
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addWebHandler( final WebHandler webHandler )
    {
        this.webDispatcher.add( webHandler );
    }

    public void removeWebHandler( final WebHandler webHandler )
    {
        this.webDispatcher.remove( webHandler );
    }
}
