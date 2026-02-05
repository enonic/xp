package com.enonic.xp.portal.impl.idprovider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.regex.MatchResult;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.handler.PathMatchers;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

import static java.util.Objects.requireNonNullElse;

class PortalRequestAdapter
{
    public PortalRequest adapt( final HttpServletRequest req )
    {
        final PortalRequest result = new PortalRequest();

        result.setRawRequest( req );
        result.setMethod( HttpMethod.valueOf( req.getMethod() ) );
        result.setScheme( req.getScheme() );
        result.setHost( req.getServerName() );
        result.setPort( req.getServerPort() );
        result.setRemoteAddress( req.getRemoteAddr() );
        result.setRawPath( req.getPathInfo() );
        result.setPath( ServletRequestUrlHelper.createUri( req, req.getRequestURI() ) );
        result.setUrl( ServletRequestUrlHelper.getFullUrl( req ) );
        result.setContentType( req.getContentType() );
        result.getLocales().addAll( Collections.list( req.getLocales() ) );

        setParameters( req, result );
        setHeaders( req, result );
        setCookies( req, result );

        baseUri( result );
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

    private static void baseUri( PortalRequest result )
    {
        final String basePath = result.getBasePath();
        if ( basePath.startsWith( PathMatchers.ADMIN_SITE_PREFIX ) )
        {
            final MatchResult matcher = PathMatchers.adminSite( result );
            if ( matcher.hasMatch() )
            {
                final RepositoryId repositoryId;
                final Branch branch;
                final RenderMode mode;
                try
                {
                    mode = RenderMode.from( matcher.group( "mode" ) );
                    repositoryId = RepositoryUtils.fromContentRepoName( matcher.group( "project" ) );
                    branch = Branch.from( matcher.group( "branch" ) );
                }
                catch ( IllegalArgumentException e )
                {
                    return;
                }

                result.setBaseUri( matcher.group( "base" ) );
                result.setMode( mode );
                result.setRepositoryId( repositoryId );
                result.setBranch( branch );
            }
        }
        else if ( basePath.equals( PathMatchers.ADMIN_TOOL_BASE ) )
        {
            result.setBaseUri( PathMatchers.ADMIN_TOOL_BASE );
        }
        else if ( basePath.startsWith( PathMatchers.ADMIN_TOOL_PREFIX ) )
        {
            final MatchResult matcher = PathMatchers.adminTool( result );
            if ( matcher.hasMatch() )
            {
                result.setBaseUri( matcher.group( "base" ) );
            }
            else
            {
                result.setBaseUri( PathMatchers.ADMIN_TOOL_BASE );
            }
        }
        else if ( basePath.startsWith( PathMatchers.SITE_PREFIX ) )
        {
            final MatchResult matcher = PathMatchers.site( result );
            if ( matcher.hasMatch() )
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

                result.setBaseUri( PathMatchers.SITE_BASE );
                result.setRepositoryId( repositoryId );
                result.setBranch( branch );
            }
        }
        else if ( basePath.startsWith( PathMatchers.WEBAPP_PREFIX ) )
        {
            final MatchResult matcher = PathMatchers.webapp( result );

            if ( matcher.hasMatch() )
            {
                result.setBaseUri( matcher.group( "base" ) );
            }
        }
        else if ( basePath.startsWith( PathMatchers.API_PREFIX ) )
        {
            final MatchResult matcher = PathMatchers.api( result );
            if ( matcher.hasMatch() )
            {
                result.setBaseUri( matcher.group( "base" ) );
            }
        }
    }
}
