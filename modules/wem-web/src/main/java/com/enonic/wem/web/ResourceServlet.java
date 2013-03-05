package com.enonic.wem.web;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.Resources;

public final class ResourceServlet
    extends HttpServlet
{
    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final URL url = findResource( req );
        if ( url != null )
        {
            serveResource( res, url );
        }
        else
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    private URL findResource( final HttpServletRequest req )
        throws IOException
    {
        final String path = req.getRequestURI().substring( req.getContextPath().length() );
        return getServletContext().getResource( path );
    }

    private void serveResource( final HttpServletResponse res, final URL url )
        throws IOException
    {
        res.setContentType( getServletContext().getMimeType( url.getPath() ) );
        Resources.copy( url, res.getOutputStream() );
    }
}
