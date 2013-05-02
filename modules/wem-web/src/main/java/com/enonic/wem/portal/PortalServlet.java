package com.enonic.wem.portal;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpMethod;

public final class PortalServlet
    extends HttpServlet
{
    private final static List<HttpMethod> ALLOWED_HTTP_METHODS =
        Arrays.asList( HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD, HttpMethod.OPTIONS );

    @Override
    public void init( final ServletConfig config )
        throws ServletException
    {
        super.init( config );
    }

    @Override
    protected void doOptions( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        response.setHeader( "Allow", StringUtils.join( ALLOWED_HTTP_METHODS, "," ) );
        response.setStatus( HttpServletResponse.SC_OK );
    }

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse resp )
        throws ServletException, IOException
    {
        // TODO: we need to dispatch
        // If not, we need to fetch content
        // Then we take Berlin

        resp.getWriter().append( "This is my portal: " + req.getRequestURI() );
        resp.setStatus( HttpServletResponse.SC_OK );
    }

    @Override
    protected void doPost( final HttpServletRequest req, final HttpServletResponse resp )
        throws ServletException, IOException
    {
        super.doGet( req, resp );
    }

    /*
    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final HttpMethod requestMethod = HttpMethod.valueOf( req.getMethod() );

        if ( !ALLOWED_HTTP_METHODS.contains( requestMethod ) )
        {
            res.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
            return;
        }

        if ( requestMethod.equals( HttpMethod.OPTIONS ) )
        {
            doOptions( req, res );
            return;
        }

        ServletRequestAccessor.setRequest( req );

        // resolve and set original url if not set
        if ( req.getAttribute( Attribute.ORIGINAL_URL ) == null )
        {
            final String originalUrl = OriginalUrlResolver.get().resolveOriginalUrl( req );
            req.setAttribute( Attribute.ORIGINAL_URL, originalUrl );
        }

        this.dispatcher.handle( req, res );
    }
    */
}
