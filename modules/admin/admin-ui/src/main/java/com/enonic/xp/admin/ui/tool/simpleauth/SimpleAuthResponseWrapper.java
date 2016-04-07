package com.enonic.xp.admin.ui.tool.simpleauth;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import com.enonic.xp.admin.ui.tool.UriScriptHelper;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

public class SimpleAuthResponseWrapper
    extends HttpServletResponseWrapper
{
    private final static Logger LOG = LoggerFactory.getLogger( SimpleAuthResponseWrapper.class );

    private final HttpServletRequest request;

    private boolean redirected;

    public SimpleAuthResponseWrapper( final HttpServletRequest req, final HttpServletResponse response )
    {
        super( response );
        this.request = req;
    }

    @Override
    public void setStatus( final int sc )
    {
        if ( 403 == sc )
        {
            try
            {
                redirect();
            }
            catch ( IOException e )
            {
                LOG.error( "Failed to redirect to the login admin tool", e );
            }
        }
        else
        {
            super.setStatus( sc );
        }
    }

    @Override
    public PrintWriter getWriter()
        throws IOException
    {
        if ( redirected )
        {
            return new PrintWriter( new StringWriter() );
        }
        return super.getWriter();
    }

    @Override
    public ServletOutputStream getOutputStream()
        throws IOException
    {
        if ( redirected )
        {
            return new ServletOutputStream()
            {
                @Override
                public boolean isReady()
                {
                    return true;
                }

                @Override
                public void setWriteListener( final WriteListener writeListener )
                {

                }

                @Override
                public void write( final int b )
                    throws IOException
                {

                }
            };
        }
        return super.getOutputStream();
    }

    @Override
    public void setHeader( final String name, final String value )
    {
        if ( !redirected )
        {
            super.setHeader( name, value );
        }
    }

    @Override
    public void sendError( final int sc )
        throws IOException
    {
        if ( 403 == sc )
        {
            redirect();
        }
        else
        {
            super.sendError( sc );
        }
    }

    @Override
    public void sendError( final int sc, final String msg )
        throws IOException
    {
        if ( 403 == sc )
        {
            redirect();
        }
        else
        {
            super.sendError( sc, msg );
        }
    }

    private void redirect()
        throws IOException
    {
        StringBuffer uri = new StringBuffer( ServletRequestUrlHelper.createUri( request.getRequestURI() ) );
        if ( !Strings.isNullOrEmpty( request.getQueryString() ) )
        {
            uri.append( "?" ).
                append( request.getQueryString() );
        }
        final String callbackUri = URLEncoder.encode( uri.toString(), "UTF-8" );

        sendRedirect( UriScriptHelper.generateAdminToolUri( "com.enonic.xp.admin.ui", "login" ) + "?callback=" + callbackUri );
        redirected = true;
    }
}
