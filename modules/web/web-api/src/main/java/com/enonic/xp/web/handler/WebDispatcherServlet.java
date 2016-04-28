package com.enonic.xp.web.handler;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public class WebDispatcherServlet
    extends HttpServlet
{

    @Override
    protected void service( final HttpServletRequest servletRequest, final HttpServletResponse servletResponse )
        throws ServletException, IOException
    {
        //Generates the Web request
        WebRequest webRequest = generateWebRequest( servletRequest, servletResponse );

        //Handles the request

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
        final boolean webSocket = isWebSocket( servletRequest, servletResponse );

        return WebRequestImpl.create().
            method( httpMethod ).
            scheme( scheme ).
            host( host ).
            port( port ).
            path( path ).
            params( params ).
            url( url ).
            cookies( cookies ).
            body( body ).
            rawRequest( servletRequest ).
            contentType( contentType ).
            webSocket( webSocket ).
            build();
    }

    private Multimap<String, String> generateWebRequestParams( final HttpServletRequest servletRequest )
    {
        return null; //TODO
    }

    private ImmutableMap<String, String> generateWebRequestCookies( final HttpServletRequest servletRequest )
    {
        return null; //TODO
    }

    private boolean isWebSocket( final HttpServletRequest servletRequest, final HttpServletResponse servletResponse )
    {
        return false; //TODO final WebSocketContext webSocketContext = webSocketContextFactory.newContext( servletRequest, servletResponse );
    }
}
