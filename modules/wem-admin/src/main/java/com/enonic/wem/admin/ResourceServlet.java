package com.enonic.wem.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;

@Singleton
public final class ResourceServlet
    extends HttpServlet
{
    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final String path = req.getRequestURI().substring( req.getContextPath().length() );
        final InputStream in = findResource( path );

        if ( in != null )
        {
            final long resourceLastModified = getResourceLastModified( path );

            if ( resourceLastModified > 0 )
            {
                if ( req.getHeader( "If-Modified-Since" ) != null )
                {
                    final long ifModifiedSince = req.getDateHeader( "If-Modified-Since" );
                    if ( resourceLastModified <= ifModifiedSince )
                    {
                        res.setStatus( HttpServletResponse.SC_NOT_MODIFIED );
                        return;
                    }
                }
                res.setDateHeader( "Last-Modified", resourceLastModified );
            }

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

        return getClass().getClassLoader().getResourceAsStream( "web/" + path );
    }

    private long getResourceLastModified( final String path )
    {
        final String resourceRealPath = getServletContext().getRealPath( path );
        final Path resourcePath = Paths.get( resourceRealPath );
        try
        {
            final FileTime lastModified = Files.getLastModifiedTime( resourcePath );
            return lastModified.toMillis();
        }
        catch ( IOException e )
        {
            return 0;
        }
    }
}
