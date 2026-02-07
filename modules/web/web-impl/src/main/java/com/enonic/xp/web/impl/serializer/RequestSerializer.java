package com.enonic.xp.web.impl.serializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public final class RequestSerializer
{
    private final WebRequest webRequest;

    public RequestSerializer( final WebRequest webRequest )
    {
        this.webRequest = webRequest;
    }

    public void serialize( final HttpServletRequest request )
    {
        webRequest.setRawRequest( request );
        webRequest.setMethod( HttpMethod.valueOf( request.getMethod() ) );
        webRequest.setScheme( request.getScheme() );
        webRequest.setHost( request.getServerName() );
        webRequest.setPort( request.getServerPort() );
        webRequest.setRemoteAddress( request.getRemoteAddr() );
        webRequest.setRawPath( request.getPathInfo() );
        webRequest.setPath( ServletRequestUrlHelper.createUri( request, request.getRequestURI() ) );
        webRequest.setUrl( ServletRequestUrlHelper.getFullUrl( request ) );
        webRequest.setContentType( request.getContentType() );
        webRequest.getLocales().addAll( Collections.list( request.getLocales() ) );
        setParameters( request, webRequest );
        setHeaders( request, webRequest );
        setCookies( request, webRequest );
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
            to.getParams().putAll( entry.getKey(), Arrays.asList( entry.getValue() ) );
        }
    }
}
