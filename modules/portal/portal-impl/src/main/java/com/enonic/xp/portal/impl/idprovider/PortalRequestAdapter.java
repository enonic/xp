package com.enonic.xp.portal.impl.idprovider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

import static java.util.Objects.requireNonNullElse;

class PortalRequestAdapter
{
    private static final String WEBAPP_PREFIX = "/webapp/";

    private static final String SITE_BASE = "/site";

    private static final String SITE_PREFIX = SITE_BASE + "/";

    private static final String ADMIN_SITE_PREFIX = "/admin/site/";

    private static final String ADMIN_TOOL_BASE = "/admin";

    private static final String ADMIN_TOOL_PREFIX = ADMIN_TOOL_BASE + "/";

    public PortalRequest adapt( final HttpServletRequest req )
    {
        final PortalRequest result = new PortalRequest();

        result.setRawRequest( req );
        result.setMethod( HttpMethod.valueOf( req.getMethod().toUpperCase( Locale.ROOT ) ) );
        result.setScheme( req.getScheme() );
        result.setHost( req.getServerName() );
        result.setPort( req.getServerPort() );
        result.setRemoteAddress( req.getRemoteAddr() );
        result.setRawPath( req.getPathInfo() );
        result.setPath( ServletRequestUrlHelper.getPath( req ) );
        result.setUrl( ServletRequestUrlHelper.getFullUrl( req ) );
        result.setContentType( req.getContentType() );

        setParameters( req, result );
        setHeaders( req, result );
        setCookies( req, result );

        baseUri( req, result );
        return result;
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

    private static void baseUri( final HttpServletRequest from, PortalRequest result )
    {
        final String requestURI = from.getRequestURI();

        if ( requestURI.equals( ADMIN_TOOL_BASE ) )
        {
            result.setBaseUri( ADMIN_TOOL_BASE );
            result.setMode( RenderMode.ADMIN );

        }
        else if ( requestURI.startsWith( ADMIN_TOOL_PREFIX ) )
        {
            String subPath = subPath( requestURI, ADMIN_TOOL_PREFIX );
            final Matcher matcher = Pattern.compile( "^([^/]+)/([^/]+)" ).matcher( subPath );
            if ( matcher.find() )
            {
                result.setBaseUri( ADMIN_TOOL_PREFIX + matcher.group( 0 ) );
                result.setMode( RenderMode.ADMIN );
            }
            else
            {
                result.setBaseUri( ADMIN_TOOL_BASE );
                result.setMode( RenderMode.ADMIN );
            }
        }
        else if ( requestURI.startsWith( ADMIN_SITE_PREFIX ) )
        {
            String subPath = subPath( requestURI, ADMIN_SITE_PREFIX );

            final Matcher matcher =
                Pattern.compile( "^(?<mode>edit|preview|admin|inline)/(?<project>[^/]+)/(?<branch>[^/]+)" ).matcher( subPath );
            if ( matcher.find() )
            {
                final RepositoryId repositoryId;
                final Branch branch;
                final RenderMode mode;
                try
                {
                    repositoryId = RepositoryUtils.fromContentRepoName( matcher.group( "project" ) );
                    branch = Branch.from( matcher.group( "branch" ) );
                    mode = RenderMode.from( matcher.group( "mode" ), RenderMode.ADMIN );
                }
                catch ( IllegalArgumentException e )
                {
                    return;
                }

                result.setBaseUri( ADMIN_SITE_PREFIX + mode );
                result.setMode( mode );
                result.setRepositoryId( repositoryId );
                result.setBranch( branch );
            }
        }
        else if ( requestURI.startsWith( SITE_PREFIX ) )
        {
            String subPath = subPath( requestURI, SITE_PREFIX );
            final Matcher matcher = Pattern.compile( "^(?<project>[^/]+)/(?<branch>[^/]+)" ).matcher( subPath );
            if ( matcher.find() )
            {
                final RepositoryId repositoryId;
                final Branch branch;
                try
                {
                    repositoryId = RepositoryUtils.fromContentRepoName( matcher.group( "project" ) );
                    branch = Branch.from( matcher.group( "branch" ) );
                }
                catch ( IllegalArgumentException e )
                {
                    return;
                }

                result.setBaseUri( SITE_BASE );
                result.setRepositoryId( repositoryId );
                result.setBranch( branch );

            }
        }
        else if ( requestURI.startsWith( WEBAPP_PREFIX ) )
        {
            String subPath = subPath( requestURI, WEBAPP_PREFIX );
            final Matcher matcher = Pattern.compile( "^([^/]+)" ).matcher( subPath );

            if ( matcher.find() )
            {
                result.setBaseUri( WEBAPP_PREFIX + matcher.group( 0 ) );
            }
        }
    }

    private static String subPath( String requestURI, String prefix )
    {
        int endpoint = requestURI.indexOf( "/_/" );
        final int endIndex = endpoint == -1 ? requestURI.length() : endpoint + 1;
        return requestURI.substring( prefix.length(), endIndex );
    }
}
