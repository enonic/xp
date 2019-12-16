package com.enonic.xp.web.impl.handler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.impl.serializer.RequestBodyReader;
import com.enonic.xp.web.serializer.ResponseSerializationService;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

@Component(immediate = true, service = Servlet.class, property = {"connector=xp"})
@Order(100)
@WebServlet("/*")
public final class WebDispatcherServlet
    extends HttpServlet
{
    private WebDispatcher webDispatcher;

    private ExceptionMapper exceptionMapper;

    private ExceptionRenderer exceptionRenderer;

    private WebSocketContextFactory webSocketContextFactory;

    private ResponseSerializationService responseSerializationService;

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
        result.setMethod( HttpMethod.valueOf( req.getMethod().toUpperCase() ) );

        final String rawPath = decodeUrl( req.getRequestURI() );
        result.setEndpointPath( findEndpointPath( rawPath ) );
        result.setRawRequest( req );
        result.setContentType( req.getContentType() );
        result.setBody( RequestBodyReader.readBody( req ) );

        result.setScheme( ServletRequestUrlHelper.getScheme( req ) );
        result.setHost( ServletRequestUrlHelper.getHost( req ) );
        result.setPort( ServletRequestUrlHelper.getPort( req ) );
        result.setRemoteAddress( ServletRequestUrlHelper.getRemoteAddress( req ) );
        result.setPath( ServletRequestUrlHelper.getPath( req ) );
        result.setRawPath( rawPath );
        result.setUrl( ServletRequestUrlHelper.getFullUrl( req ) );

        setParameters( req, result );
        setHeaders( req, result );
        setCookies( req, result );

        return result;
    }

    private void setHeaders( final HttpServletRequest from, final WebRequest to )
    {
        for ( final String key : Collections.list( from.getHeaderNames() ) )
        {
            to.getHeaders().put( key, from.getHeader( key ) );
        }
    }

    private void setCookies( final HttpServletRequest from, final WebRequest to )
    {
        final Cookie[] cookies = from.getCookies();
        if ( cookies == null )
        {
            return;
        }

        for ( final Cookie cookie : cookies )
        {
            to.getCookies().put( cookie.getName(), cookie.getValue() );
        }
    }

    private void setParameters( final HttpServletRequest from, final WebRequest to )
    {
        for ( final Map.Entry<String, String[]> entry : from.getParameterMap().entrySet() )
        {
            to.getParams().putAll( entry.getKey(), new ArrayList<>( Arrays.asList( entry.getValue() ) ) );
        }
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
        final WebResponse webResponse = this.exceptionRenderer.render( req, exception );
        return webResponse;
    }

    private WebResponse filterResponse( final WebRequest webRequest, final WebResponse webResponse )
        throws Exception
    {
        if ( webRequest.getRawRequest().getAttribute( "error.handled" ) != Boolean.TRUE )
        {
            this.exceptionMapper.throwIfNeeded( webResponse );
        }
        return webResponse;
    }

    private static String findEndpointPath( final String path )
    {
        final int endpointPathIndex = path.indexOf( "/_/" );
        return endpointPathIndex > -1 ? path.substring( endpointPathIndex ) : "";
    }

    private static String decodeUrl( final String url )
    {
        return URLDecoder.decode( url, StandardCharsets.UTF_8 );
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

    @Reference
    public void setWebDispatcher( final WebDispatcher webDispatcher )
    {
        this.webDispatcher = webDispatcher;
    }
}
