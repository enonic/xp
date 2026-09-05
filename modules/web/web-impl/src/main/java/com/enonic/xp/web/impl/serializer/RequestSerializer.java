package com.enonic.xp.web.impl.serializer;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.google.common.base.Splitter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class RequestSerializer
{
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

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
        if ( isMultipartFormData( from.getContentType() ) )
        {
            addQueryParameters( from.getQueryString(), to );
            return;
        }

        for ( final Map.Entry<String, String[]> entry : from.getParameterMap().entrySet() )
        {
            to.getParams().putAll( entry.getKey(), Arrays.asList( entry.getValue() ) );
        }
    }

    static boolean isMultipartFormData( final String contentType )
    {
        if ( contentType == null )
        {
            return false;
        }
        final int parameters = contentType.indexOf( ';' );
        final String mediaType = parameters < 0 ? contentType : contentType.substring( 0, parameters );
        return MULTIPART_FORM_DATA.equalsIgnoreCase( mediaType.trim() );
    }

    static void addQueryParameters( final String queryString, final WebRequest to )
    {
        if ( isNullOrEmpty( queryString ) )
        {
            return;
        }

        try
        {
            for ( final String pair : Splitter.on( '&' ).omitEmptyStrings().split( queryString ) )
            {
                final int separator = pair.indexOf( '=' );
                final String name = separator < 0 ? pair : pair.substring( 0, separator );
                final String value = separator < 0 ? "" : pair.substring( separator + 1 );
                to.getParams()
                    .put( URLDecoder.decode( name, StandardCharsets.UTF_8 ), URLDecoder.decode( value, StandardCharsets.UTF_8 ) );
            }
        }
        catch ( IllegalArgumentException e )
        {
            throw WebException.badRequest( "Malformed query string", e );
        }
    }
}
