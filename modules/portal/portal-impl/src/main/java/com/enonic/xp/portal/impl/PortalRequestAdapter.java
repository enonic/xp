package com.enonic.xp.portal.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.PortalAttributes;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

import static java.util.Objects.requireNonNullElse;

public class PortalRequestAdapter
{
    public static final String PORTAL_BASE_URI = "/site";

    public static final String ADMIN_BASE_URI = "/admin/site/admin";

    public PortalRequest adapt( final HttpServletRequest req )
    {
        final PortalRequest result = new PortalRequest();

        final PortalAttributes portalAttributes = (PortalAttributes) req.getAttribute( PortalAttributes.class.getName() );

        getRenderMode( portalAttributes ).ifPresent( result::setMode );
        result.setBaseUri( getBaseUri( portalAttributes ).orElseGet( () -> requestUriToBaseUri( req.getRequestURI() ) ) );
        result.setMethod( HttpMethod.valueOf( req.getMethod().toUpperCase( Locale.ROOT ) ) );
        result.setRawRequest( req );
        result.setContentType( req.getContentType() );
        result.setScheme( req.getScheme() );
        result.setHost( req.getServerName() );
        result.setRemoteAddress( req.getRemoteAddr() );
        result.setPort( req.getServerPort() );
        result.setPath( ServletRequestUrlHelper.getPath( req ) );
        result.setRawPath( req.getPathInfo() );
        result.setUrl( ServletRequestUrlHelper.getFullUrl( req ) );

        setParameters( req, result );
        setHeaders( req, result );
        setCookies( req, result );

        return result;
    }

    private static Optional<RenderMode> getRenderMode( final PortalAttributes portalAttributes )
    {
        return Optional.ofNullable( portalAttributes ).map( PortalAttributes::getRenderMode );
    }

    private static Optional<String> getBaseUri( final PortalAttributes portalAttributes )
    {
        return Optional.ofNullable( portalAttributes ).map( PortalAttributes::getBaseUri );
    }

    private static String requestUriToBaseUri( final String requestUri )
    {
        if ( requestUri.equals( "/site" ) || requestUri.startsWith( "/site/" ) )
        {
            return PORTAL_BASE_URI;
        }
        else if ( requestUri.equals( "/admin" ) || requestUri.startsWith( "/admin/" ) )
        {
            return ADMIN_BASE_URI;
        }
        else
        {
            return requestUri;
        }
    }

    private static void setHeaders( final HttpServletRequest from, final PortalRequest to )
    {
        for ( final String key : Collections.list( requireNonNullElse( from.getHeaderNames(), Collections.emptyEnumeration() ) ) )
        {
            to.getHeaders().put( key, from.getHeader( key ) );
        }
    }

    private static void setCookies( final HttpServletRequest from, final PortalRequest to )
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

    private static void setParameters( final HttpServletRequest from, final PortalRequest to )
    {
        for ( final Map.Entry<String, String[]> entry : from.getParameterMap().entrySet() )
        {
            to.getParams().putAll( entry.getKey(), Arrays.asList( entry.getValue() ) );
        }
    }
}
