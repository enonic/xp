package com.enonic.xp.portal.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalAttributes;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNullElse;

public class PortalRequestAdapter
{

    public PortalRequest adapt( final HttpServletRequest req )
    {
        final PortalRequest result = new PortalRequest();

        final PortalAttributes portalAttributes = (PortalAttributes) req.getAttribute( PortalAttributes.class.getName() );

        final String baseUri = getBaseUri( portalAttributes ).orElseGet( () -> requestUriToBaseUri( req.getRequestURI() ) );
        final Optional<RenderMode> renderMode = getRenderMode( portalAttributes );

        result.setBaseUri( baseUri );

        if ( isSiteBase( baseUri ) )
        {
            final String baseSubPath = req.getRequestURI().substring( baseUri.length() + 1 );

            result.setRepositoryId( findRepository( baseSubPath ) );
            result.setBranch( findBranch( baseSubPath ) );
        }
        else
        {
            renderMode.ifPresent( result::setMode );
        }
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
            return "/site";
        }
        else if ( requestUri.equals( "/admin/site" ) || requestUri.startsWith( "/admin/site/" ) )
        {
            return "/admin/site/admin";
        }
        else
        {
            return requestUri;
        }
    }

    private static boolean isSiteBase( final String baseUri )
    {
        return baseUri.equals( "/site" ) || baseUri.equals( "/admin/site" ) || baseUri.startsWith( "/site/" ) ||
            baseUri.startsWith( "/admin/site/" );
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

    private static RepositoryId findRepository( final String baseSubPath )
    {
        final int index = baseSubPath.indexOf( '/' );
        final String result = baseSubPath.substring( 0, index > 0 ? index : baseSubPath.length() );
        if ( result.isEmpty() )
        {
            throw WebException.notFound( "Repository needs to be specified" );
        }
        return RepositoryUtils.fromContentRepoName( result );
    }

    private static Branch findBranch( final String baseSubPath )
    {
        final String branchSubPath = findPathAfterRepository( baseSubPath );
        final int index = branchSubPath.indexOf( '/' );
        final String result = branchSubPath.substring( 0, index > 0 ? index : branchSubPath.length() );
        if ( isNullOrEmpty( result ) )
        {
            throw WebException.notFound( "Branch needs to be specified" );
        }
        return Branch.from( result );
    }

    private static String findPathAfterRepository( final String baseSubPath )
    {
        final int index = baseSubPath.indexOf( '/' );
        return baseSubPath.substring( index > 0 && index < baseSubPath.length() ? index + 1 : baseSubPath.length() );
    }
}
