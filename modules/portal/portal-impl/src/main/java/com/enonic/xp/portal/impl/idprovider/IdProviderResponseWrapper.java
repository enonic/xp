package com.enonic.xp.portal.impl.idprovider;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;

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
        try
        {
            handleError( sc );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

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
            return new PrintWriter( Writer.nullWriter() );
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
        throws IOException
    {
        if ( !errorHandled && isUnauthorizedError( sc ) && !isErrorAlreadyHandled() )
        {
            final IdProviderControllerExecutionParams executionParams = IdProviderControllerExecutionParams.create()
                .functionName( "handle401" )
                .servletRequest( request )
                .response( response )
                .build();
            final boolean responseSerialized = idProviderControllerService.execute( executionParams ) != null;
            if ( responseSerialized )
            {
                errorHandled = true;
            }
        }
    }

    private boolean isUnauthorizedError( final int sc )
    {
        return 401 == sc || 403 == sc && !ContextAccessor.current().getAuthInfo().isAuthenticated();
    }

    private boolean isErrorAlreadyHandled()
    {
        return Boolean.TRUE.equals( request.getAttribute( "error.handled" ) );
    }
}
