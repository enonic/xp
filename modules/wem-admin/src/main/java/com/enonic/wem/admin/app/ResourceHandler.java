package com.enonic.wem.admin.app;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;

final class ResourceHandler
{
    private final ServletContext servletContext;

    public ResourceHandler( final ServletContext servletContext )
    {
        this.servletContext = servletContext;
    }

    public void handle( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final String path = req.getRequestURI().substring( req.getContextPath().length() );
        final InputStream in = findResource( path );

        if ( in != null )
        {
            serveResource( res, path, in );
        }
        else
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    private void serveResource( final HttpServletResponse res, final String path, final InputStream in )
        throws IOException
    {
        res.setContentType( this.servletContext.getMimeType( path ) );
        ByteStreams.copy( in, res.getOutputStream() );
    }

    private InputStream findResource( final String path )
    {
        if ( path.endsWith( "/" ) )
        {
            return findResource( path + "index.html" );
        }

        final InputStream in = this.servletContext.getResourceAsStream( path );
        if ( in != null )
        {
            return in;
        }

        final String resourcePath = "web" + ( path.startsWith( "/" ) ? path : ( "/" + path ) );
        return getClass().getClassLoader().getResourceAsStream( resourcePath );
    }
}
