package com.enonic.xp.portal.impl;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.serializer.ResponseSerializer;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@Component(immediate = true, service = WebHandler.class)
public final class PortalDispatcher
    extends BaseWebHandler
{
    private final static String BASE_URI = "/portal2";

    private final static String PATH_PREFIX = BASE_URI + "/";

    private final PortalHandlerRegistry registry;

    public PortalDispatcher()
    {
        this.registry = new PortalHandlerRegistry();
        setOrder( -1 );
    }

    @Override
    protected boolean canHandle( final HttpServletRequest req )
    {
        return req.getRequestURI().startsWith( PATH_PREFIX );
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        final PortalRequest portalRequest = newPortalRequest( req );
        final PortalResponse portalResponse = doHandle( portalRequest );

        final ResponseSerializer serializer = new ResponseSerializer( portalRequest, portalResponse );
        serializer.serialize( res );
    }

    private PortalRequest newPortalRequest( final HttpServletRequest req )
    {
        final PortalRequest result = new PortalRequest();
        result.setMethod( req.getMethod() );
        result.setBaseUri( BASE_URI );

        final String rawPath = req.getPathInfo();
        result.setBranch( findBranch( rawPath ) );
        result.setEndpointPath( findEndpointPath( rawPath ) );
        result.setContentPath( findContentPath( rawPath ) );

        result.setScheme( ServletRequestUrlHelper.getScheme( req ) );
        result.setHost( ServletRequestUrlHelper.getHost( req ) );
        result.setPort( ServletRequestUrlHelper.getPort( req ) );
        result.setPath( ServletRequestUrlHelper.getPath( req ) );
        result.setUrl( ServletRequestUrlHelper.getFullUrl( req ) );

        setParameters( req, result );
        setHeaders( req, result );
        setCookies( req, result );

        return result;
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
        for ( final Cookie cookie : from.getCookies() )
        {
            to.getCookies().put( cookie.getName(), cookie.getValue() );
        }
    }

    private void setParameters( final HttpServletRequest from, final PortalRequest to )
    {
        for ( final Map.Entry<String, String[]> entry : from.getParameterMap().entrySet() )
        {
            to.getParams().putAll( entry.getKey(), Lists.newArrayList( entry.getValue() ) );
        }
    }

    private PortalResponse doHandle( final PortalRequest req )
        throws Exception
    {
        if ( req.getBranch() == null )
        {
            throw PortalException.notFound( "Branch needs to be specified" );
        }

        final PortalHandler handler = this.registry.find( req );
        return handler.handle( req );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addHandler( final PortalHandler handler )
    {
        this.registry.add( handler );
    }

    public void removeHandler( final PortalHandler handler )
    {
        this.registry.remove( handler );
    }

    private static Branch findBranch( final String path )
    {
        final int index = path.indexOf( '/', PATH_PREFIX.length() );
        final String result = path.substring( PATH_PREFIX.length(), index > 0 ? index : path.length() );
        return Strings.isNullOrEmpty( result ) ? null : Branch.from( result );
    }

    private static ContentPath findContentPath( final String path )
    {
        final String restPath = findPathAfterBranch( path );
        final int underscore = restPath.indexOf( "/_/" );
        final String result = restPath.substring( 0, underscore > -1 ? underscore : restPath.length() );
        return ContentPath.from( result.startsWith( "/" ) ? result : ( "/" + result ) );
    }

    private static String findEndpointPath( final String path )
    {
        final String restPath = findPathAfterBranch( path );
        final int underscore = restPath.indexOf( "/_/" );
        return restPath.substring( underscore > -1 ? underscore : restPath.length() );
    }

    private static String findPathAfterBranch( final String path )
    {
        final int index = path.indexOf( '/', PATH_PREFIX.length() );
        return path.substring( index > 0 ? index : path.length() );
    }
}
