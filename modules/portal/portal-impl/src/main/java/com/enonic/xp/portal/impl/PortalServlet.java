package com.enonic.xp.portal.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalRequestAdapter;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.PortalHandler;
import com.enonic.xp.portal.impl.exception.ExceptionMapper;
import com.enonic.xp.portal.impl.exception.ExceptionRenderer;
import com.enonic.xp.portal.impl.serializer.ResponseSerializer;
import com.enonic.xp.portal.impl.websocket.WebSocketContext;
import com.enonic.xp.portal.impl.websocket.WebSocketContextFactory;
import com.enonic.xp.portal.websocket.WebSocketConfig;
import com.enonic.xp.portal.websocket.WebSocketEndpoint;

@Component(immediate = true, service = Servlet.class,
    property = {"osgi.http.whiteboard.servlet.pattern=/portal/*"})
public final class PortalServlet
    extends HttpServlet
{
    private final static String PATH_PREFIX = PortalRequestAdapter.BASE_URI + "/";

    private final ExceptionMapper exceptionMapper;

    private final PortalHandlerRegistry registry;

    private ExceptionRenderer exceptionRenderer;

    private WebSocketContextFactory webSocketContextFactory;

    public PortalServlet()
    {
        this.exceptionMapper = new ExceptionMapper();
        this.registry = new PortalHandlerRegistry();
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final PortalRequest portalRequest = newPortalRequest( req );
        final WebSocketContext webSocketContext = this.webSocketContextFactory.newContext( req, res );
        portalRequest.setWebSocket( webSocketContext != null );

        final PortalResponse portalResponse = doHandle( portalRequest );
        final WebSocketConfig config = portalResponse.getWebSocket();

        if ( ( webSocketContext != null ) && ( config != null ) )
        {
            applyWebSocket( portalRequest, webSocketContext, config );
            return;
        }

        final ResponseSerializer serializer = new ResponseSerializer( portalRequest, portalResponse );
        serializer.serialize( res );
    }

    private void applyWebSocket( final PortalRequest req, final WebSocketContext context, final WebSocketConfig config )
    {
        try
        {
            if ( req.getBranch() == null )
            {
                throw PortalException.notFound( "Branch needs to be specified" );
            }

            final PortalHandler handler = this.registry.find( req );
            ContextAccessor.current().getLocalScope().setAttribute( req.getBranch() );

            final WebSocketEndpoint endpoint = handler.newWebSocketEndpoint( req, config );
            context.apply( endpoint );
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
    }

    private PortalRequest newPortalRequest( final HttpServletRequest req )
        throws IOException
    {
        final PortalRequest result = new PortalRequestAdapter().
            adapt( req );

        final String rawPath = decodeUrl( req.getRequestURI() );
        result.setBranch( findBranch( rawPath ) );
        result.setEndpointPath( findEndpointPath( rawPath ) );
        result.setContentPath( findContentPath( rawPath ) );

        PortalRequestAccessor.set( req, result );

        return result;
    }

    private PortalResponse doHandle( final PortalRequest req )
    {
        try
        {
            if ( req.getBranch() == null )
            {
                throw PortalException.notFound( "Branch needs to be specified" );
            }

            final PortalHandler handler = this.registry.find( req );

            ContextAccessor.current().getLocalScope().setAttribute( req.getBranch() );
            return filterResponse( handler.handle( req ) );
        }
        catch ( final Exception e )
        {
            return handleError( req, e );
        }
    }

    private PortalResponse handleError( final PortalRequest req, final Exception cause )
    {
        final PortalException exception = this.exceptionMapper.map( cause );
        return this.exceptionRenderer.render( req, exception );
    }

    private PortalResponse filterResponse( final PortalResponse res )
        throws Exception
    {
        this.exceptionMapper.throwIfNeeded( res );
        return res;
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

    private static String decodeUrl( final String url )
    {
        try
        {
            return URLDecoder.decode( url, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw PortalException.internalServerError( "Error while decoding URL: " + url );
        }
    }

    @Reference
    public void setExceptionRenderer( final ExceptionRenderer exceptionRenderer )
    {
        this.exceptionRenderer = exceptionRenderer;
    }

    @Reference
    public void setWebSocketContextFactory( final WebSocketContextFactory webSocketContextFactory )
    {
        this.webSocketContextFactory = webSocketContextFactory;
    }
}
