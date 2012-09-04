package com.enonic.wem.web.filter.bundle;

import java.io.IOException;
import java.net.URL;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.enonic.wem.web.filter.bundle.processor.BundleProcessor;

public final class BundleFilter
    extends OncePerRequestFilter
{
    private final static String JS_BUNDLE_SUFFIX = "/bundle.js";

    private BundleProcessor processor;

    @Override
    protected void doFilterInternal( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws ServletException, IOException
    {
        final String path = req.getRequestURI();
        final URL bundleJsonUrl = getBundleJsonUrl( path );
        if ( bundleJsonUrl == null )
        {
            chain.doFilter( req, res );
            return;
        }

        final BundleRequest bundleRequest = new BundleRequest();
        bundleRequest.setServletContext( getServletContext() );
        bundleRequest.setBundleJsonUrl( bundleJsonUrl );
        bundleRequest.setCacheTimestamp( req.getParameter( "ts" ) );
        bundleRequest.setRequestPath( path );

        handleBundleRequest( bundleRequest, res );
    }

    private URL getBundleJsonUrl( final String path )
        throws IOException
    {
        if ( !path.startsWith( "/admin" ) )
        {
            return null;
        }

        if ( !path.endsWith( JS_BUNDLE_SUFFIX ) )
        {
            return null;
        }

        if ( getServletContext().getResource( path ) != null )
        {
            return null;
        }

        final String jsonPath = StringUtils.stripFilenameExtension( path ) + ".json";
        return getServletContext().getResource( jsonPath );
    }

    private void handleBundleRequest( final BundleRequest req, final HttpServletResponse res )
        throws IOException
    {
        try
        {
            final String content = this.processor.process( req );
            serveJavaScript( content, res );
        }
        catch ( final IOException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new IOException( e );
        }
    }

    private void serveJavaScript( final String content, final HttpServletResponse res )
        throws IOException
    {
        res.setContentType( "application/javascript" );
        res.getWriter().println( content );
    }

    @Override
    public void destroy()
    {
        // Do nothing
    }

    @Autowired
    public void setProcessor( final BundleProcessor processor )
    {
        this.processor = processor;
    }
}
