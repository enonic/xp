package com.enonic.xp.web.impl.handler;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebRequestImpl;
import com.enonic.xp.web.handler.WebResponse;
import com.enonic.xp.web.handler.WebResponseImpl;
import com.enonic.xp.web.impl.websocket.WebSocketContext;
import com.enonic.xp.web.impl.websocket.WebSocketContextFactory;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@Component(immediate = true, service = Servlet.class,
    property = {"osgi.http.whiteboard.servlet.pattern=/test/*"})
public class WebDispatcherServlet
    extends HttpServlet
{
    private WebDispatcher webDispatcher = new WebDispatcher();

    private WebSocketContextFactory webSocketContextFactory;

    @Override
    protected void service( final HttpServletRequest servletRequest, final HttpServletResponse servletResponse )
        throws ServletException, IOException
    {
        //Generates the Web request and response
        WebRequest webRequest = generateWebRequest( servletRequest, servletResponse );
        WebResponse webResponse = generateWebResponse();

        //Handles the request
        webDispatcher.dispatch( webRequest, webResponse );

        //Serializes the request

    }

    private WebRequest generateWebRequest( final HttpServletRequest servletRequest, final HttpServletResponse servletResponse )
    {
        final HttpMethod httpMethod = HttpMethod.valueOf( servletRequest.getMethod().toUpperCase() );
        final String contentType = servletRequest.getContentType();

        final String scheme = ServletRequestUrlHelper.getScheme( servletRequest );
        final String host = ServletRequestUrlHelper.getHost( servletRequest );
        final int port = ServletRequestUrlHelper.getPort( servletRequest );
        final String path = ServletRequestUrlHelper.getPath( servletRequest );
        final String url = ServletRequestUrlHelper.getFullUrl( servletRequest );

        final Object body = ServletRequestBodyReader.readBody( servletRequest );

        final Multimap<String, String> params = generateWebRequestParams( servletRequest );
        final ImmutableMap<String, String> cookies = generateWebRequestCookies( servletRequest );
        final String endpointPath = getEndpointPath( servletRequest );
        final boolean webSocket = isWebSocket( servletRequest, servletResponse );

        return WebRequestImpl.create().
            method( httpMethod ).
            scheme( scheme ).
            host( host ).
            port( port ).
            path( path ).
            params( params ).
            url( url ).
            endpointPath( endpointPath ).
            cookies( cookies ).
            body( body ).
            rawRequest( servletRequest ).
            contentType( contentType ).
            webSocket( webSocket ).
            build();
    }

    private WebResponse generateWebResponse()
    {
        final WebResponseImpl webResponse = new WebResponseImpl();
        webResponse.setStatus( HttpStatus.OK );
        //TODO Set missing fields
        return webResponse;
    }

    private Multimap<String, String> generateWebRequestParams( final HttpServletRequest servletRequest )
    {
        final HashMultimap<String, String> paramsMultimap = HashMultimap.create();

        servletRequest.getParameterMap().
            entrySet().
            forEach( servletRequestParameter -> {
                String key = servletRequestParameter.getKey();
                List<String> values = Arrays.asList( servletRequestParameter.getValue() );
                paramsMultimap.putAll( key, values );
            } );

        return paramsMultimap;
    }

    private ImmutableMap<String, String> generateWebRequestCookies( final HttpServletRequest servletRequest )
    {
        final ImmutableMap.Builder<String, String> cookieImmutableMap = ImmutableMap.builder();

        final Cookie[] servletRequestCookies = servletRequest.getCookies();
        if ( servletRequestCookies != null )
        {
            for ( final Cookie servletRequestCookie : servletRequestCookies )
            {
                cookieImmutableMap.put( servletRequestCookie.getName(), servletRequestCookie.getValue() );
            }
        }

        return cookieImmutableMap.build();
    }

    private String getEndpointPath( final HttpServletRequest servletRequest )
    {
        final String requestURI = servletRequest.getRequestURI();
        final int endpointPathIndex = requestURI.indexOf( "/_/" );
        return endpointPathIndex > -1 ? requestURI.substring( endpointPathIndex ) : "";
    }

    private boolean isWebSocket( final HttpServletRequest servletRequest, final HttpServletResponse servletResponse )
    {
        final WebSocketContext webSocketContext = webSocketContextFactory.newContext( servletRequest, servletResponse );
        return webSocketContext != null;
    }

    @Reference
    public void setWebSocketContextFactory( final WebSocketContextFactory webSocketContextFactory )
    {
        this.webSocketContextFactory = webSocketContextFactory;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addWebHandler( final WebHandler webHandler )
    {
        webDispatcher.add( webHandler );
    }

    public void removeWebHandler( final WebHandler webHandler )
    {
        webDispatcher.remove( webHandler );
    }
}
