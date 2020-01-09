package com.enonic.xp.portal.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.PortalAttributes;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public class PortalRequestAdapter
{
    public final static String PORTAL_BASE_URI = "/site";

    public final static String ADMIN_BASE_URI = "/admin/site/admin";

    public PortalRequest adapt( final HttpServletRequest req )
    {
        final PortalRequest result = new PortalRequest();

        setBaseUri( req, result );
        setRenderMode( req, result );

        result.setMethod( HttpMethod.valueOf( req.getMethod().toUpperCase() ) );
        result.setRawRequest( req );
        result.setContentType( req.getContentType() );

        //TODO Temporary fix until Admin/Site full refactoring
        //The Servlet request should be translated to Portal request only once
//        result.setBody( RequestBodyReader.readBody( req ) );

        result.setScheme( ServletRequestUrlHelper.getScheme( req ) );
        result.setHost( ServletRequestUrlHelper.getHost( req ) );
        result.setRemoteAddress( ServletRequestUrlHelper.getRemoteAddress( req ) );
        result.setPort( ServletRequestUrlHelper.getPort( req ) );
        result.setPath( ServletRequestUrlHelper.getPath( req ) );
        result.setRawPath( req.getRequestURI() );
        result.setUrl( ServletRequestUrlHelper.getFullUrl( req ) );

        setParameters( req, result );
        setHeaders( req, result );
        setCookies( req, result );

        return result;
    }

    private void setBaseUri( final HttpServletRequest from, final PortalRequest to )
    {
        final PortalAttributes portalAttributes = (PortalAttributes) from.getAttribute( PortalAttributes.class.getName() );
        if ( portalAttributes != null && portalAttributes.getBaseUri() != null )
        {
            to.setBaseUri( portalAttributes.getBaseUri() );
        }
        else
        {
            final String requestURI = from.getRequestURI();
            if ( requestURI.startsWith( "/site" ) )
            {
                to.setBaseUri( PORTAL_BASE_URI );
            }
            else if ( requestURI.startsWith( "/admin" ) )
            {
                to.setBaseUri( ADMIN_BASE_URI );
            }
            else
            {
                to.setBaseUri( requestURI );
            }
        }
    }

    private void setRenderMode( final HttpServletRequest from, final PortalRequest to )
    {
        final PortalAttributes portalAttributes = (PortalAttributes) from.getAttribute( PortalAttributes.class.getName() );
        if ( portalAttributes != null && portalAttributes.getRenderMode() != null )
        {
            to.setMode( portalAttributes.getRenderMode() );
        }
    }


    private void setHeaders( final HttpServletRequest from, final PortalRequest to )
    {
        for ( final String key : Collections.list( from.getHeaderNames() ) )
        {
            to.getHeaders().put( key, from.getHeader( key ) );
        }
    }

    private void setCookies( final HttpServletRequest from, final PortalRequest to )
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

    private void setParameters( final HttpServletRequest from, final PortalRequest to )
    {
        for ( final Map.Entry<String, String[]> entry : from.getParameterMap().entrySet() )
        {
            to.getParams().putAll( entry.getKey(), Arrays.asList( entry.getValue() ) );
        }
    }
}