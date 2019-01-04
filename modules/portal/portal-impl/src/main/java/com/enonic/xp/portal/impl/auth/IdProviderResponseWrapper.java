package com.enonic.xp.portal.impl.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.auth.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.auth.IdProviderControllerService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.Exceptions;


public class IdProviderResponseWrapper
    extends HttpServletResponseWrapper
{
    private final IdProviderControllerService idProviderControllerService;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private boolean errorHandled;

    public IdProviderResponseWrapper( final IdProviderControllerService idProviderControllerService, final HttpServletRequest request,
                                      final HttpServletResponse response )
    {
        super( response );
        this.idProviderControllerService = idProviderControllerService;
        this.request = request;
        this.response = response;
    }

    @Override
    public void setStatus( final int sc )
    {
        handleError( sc );

        if ( !errorHandled )
        {
            super.setStatus( sc );
        }
    }

    @Override
    public PrintWriter getWriter()
        throws IOException
    {
        if ( errorHandled )
        {
            return new PrintWriter( new StringWriter() );
        }
        return super.getWriter();
    }

    @Override
    public ServletOutputStream getOutputStream()
        throws IOException
    {
        if ( errorHandled )
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
        if ( !errorHandled )
        {
            super.setHeader( name, value );
        }
    }

    @Override
    public void sendError( final int sc )
        throws IOException
    {
        handleError( sc );

        if ( !errorHandled )
        {
            super.sendError( sc );
        }
    }

    @Override
    public void sendError( final int sc, final String msg )
        throws IOException
    {
        handleError( sc );

        if ( !errorHandled )
        {
            super.sendError( sc, msg );
        }
    }

    private void handleError( final int sc )
    {
        if ( !errorHandled && isUnauthorizedError( sc ) && !isErrorAlreadyHandled() )
        {
            try
            {
                final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create().
                    functionName( "handle401" ).
                    servletRequest( request ).
                    response( response ).
                    build();
                final boolean responseSerialized = idProviderControllerService.execute( executionParams ) != null;
                if ( responseSerialized )
                {
                    errorHandled = true;
                }
            }
            catch ( IOException e )
            {
                throw Exceptions.unchecked( e );
            }
        }
    }

    private boolean isUnauthorizedError( final int sc )
    {
        return 401 == sc || ( 403 == sc && !isAuthenticated() );
    }

    private boolean isErrorAlreadyHandled()
    {
        return Boolean.TRUE == request.getAttribute( "error.handled" );
    }


    private boolean isAuthenticated()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return authInfo.isAuthenticated();
    }
}