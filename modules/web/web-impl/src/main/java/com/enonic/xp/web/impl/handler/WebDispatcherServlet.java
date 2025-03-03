package com.enonic.xp.web.impl.handler;

import java.io.IOException;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.impl.serializer.RequestBodyReader;
import com.enonic.xp.web.impl.serializer.RequestSerializer;
import com.enonic.xp.web.serializer.ResponseSerializationService;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

@Component(immediate = true, service = Servlet.class, property = {"connector=xp"})
@Order(100)
@WebServlet("/*")
public final class WebDispatcherServlet
    extends HttpServlet
{
    private final WebDispatcher webDispatcher;

    private ExceptionMapper exceptionMapper;

    private ExceptionRenderer exceptionRenderer;

    private WebSocketContextFactory webSocketContextFactory;

    private ResponseSerializationService responseSerializationService;

    @Activate
    public WebDispatcherServlet( @Reference final WebDispatcher webDispatcher )
    {
        this.webDispatcher = webDispatcher;
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final WebRequest webRequest = newWebRequest( req );
        final WebSocketContext webSocketContext = this.webSocketContextFactory.newContext( req, res );
        webRequest.setWebSocketContext( webSocketContext );

        final WebResponse webResponse = doHandle( webRequest );

        final WebSocketConfig config = webResponse.getWebSocket();
        if ( ( webSocketContext != null ) && ( config != null ) )
        {
            return;
        }

        responseSerializationService.serialize( webRequest, webResponse, res );
    }

    private WebRequest newWebRequest( final HttpServletRequest req )
        throws IOException
    {
        final WebRequest result = new WebRequest();
        new RequestSerializer( result ).serialize( req );
        result.setBody( RequestBodyReader.readBody( req ) );

        return result;
    }

    private WebResponse doHandle( final WebRequest webRequest )
    {
        try
        {
            final WebResponse webResponse = webDispatcher.dispatch( webRequest, WebResponse.create().build() );
            return filterResponse( webRequest, webResponse );
        }
        catch ( Exception e )
        {
            return handleError( webRequest, e );
        }
    }

    private WebResponse handleError( final WebRequest req, final Exception cause )
    {
        final WebException exception = this.exceptionMapper.map( cause );
        return this.exceptionRenderer.render( req, exception );
    }

    private WebResponse filterResponse( final WebRequest webRequest, final WebResponse webResponse )
    {
        if ( !Boolean.TRUE.equals( webRequest.getRawRequest().getAttribute( "error.handled" ) ) )
        {
            this.exceptionMapper.throwIfNeeded( webResponse );
        }
        return webResponse;
    }

    @Reference
    public void setExceptionMapper( final ExceptionMapper exceptionMapper )
    {
        this.exceptionMapper = exceptionMapper;
    }

    @Reference
    public void setExceptionRenderer( final ExceptionRenderer exceptionRenderer )
    {
        this.exceptionRenderer = exceptionRenderer;
    }

    @Reference
    public void setWebSocketContextFactory( final WebSocketContextFactory webSocketContextFactory )
    {
        this.webSocketContextFactory = webSocketContextFactory;
    }

    @Reference
    public void setResponseSerializationService( final ResponseSerializationService responseSerializationService )
    {
        this.responseSerializationService = responseSerializationService;
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
