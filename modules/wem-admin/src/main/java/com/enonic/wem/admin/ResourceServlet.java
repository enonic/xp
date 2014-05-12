package com.enonic.wem.admin;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;

import com.enonic.wem.admin.app.AppServlet;

@Singleton
public final class ResourceServlet
    extends HttpServlet
{
    @Inject
    protected AppServlet appServlet;

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final String path = req.getRequestURI().substring( req.getContextPath().length() );
        if ( path.equals( "/admin" ) )
        {
            res.sendRedirect( req.getRequestURI() + "/" );
            return;
        }

        if ( path.equals( "/admin/" ) )
        {
            this.appServlet.handleGet( req, res );
            return;
        }

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
        res.setContentType( getServletContext().getMimeType( path ) );
        ByteStreams.copy( in, res.getOutputStream() );
    }

    private InputStream findResource( final String path )
    {
        if ( path.endsWith( "/" ) )
        {
            return findResource( path + "index.html" );
        }

        final InputStream in = getServletContext().getResourceAsStream( path );
        if ( in != null )
        {
            return in;
        }

        final String resourcePath = "web" + ( path.startsWith( "/" ) ? path : ( "/" + path ) );
        return getClass().getClassLoader().getResourceAsStream( resourcePath );
    }
}
