package com.enonic.xp.portal.impl.idprovider;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Iterator;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.VirtualHostHelper;

public class IdProviderResponseWrapper
    extends HttpServletResponseWrapper
{
    private final IdProviderControllerService idProviderControllerService;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private boolean errorHandled;

    private boolean bearerChallenged;

    private int pendingStatus;

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
        pendingStatus = isUnauthorizedError( sc ) ? sc : 0;
        if ( !errorHandled )
        {
            super.setStatus( sc );
        }
    }

    /**
     * Decides on the login page for a status set earlier, now that the headers set with it are known.
     * The filter calls it once the application has answered; a body or a flush calls it before.
     */
    public void complete()
    {
        if ( pendingStatus == 0 )
        {
            return;
        }
        final int sc = pendingStatus;
        pendingStatus = 0;
        try
        {
            handleError( sc );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public void flushBuffer()
        throws IOException
    {
        complete();
        super.flushBuffer();
    }

    @Override
    public PrintWriter getWriter()
        throws IOException
    {
        complete();
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
        complete();
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
            super.setHeader( name, challenge( name, value ) );
        }
    }

    @Override
    public void addHeader( final String name, final String value )
    {
        if ( !errorHandled )
        {
            super.addHeader( name, challenge( name, value ) );
        }
    }

    /**
     * A bearer challenge is the application's choice of a client at a machine, not a person at a
     * browser: it is completed with the resource metadata URL and the login page stays out of it.
     */
    private String challenge( final String name, final String value )
    {
        if ( BearerChallenge.HEADER.equalsIgnoreCase( name ) && BearerChallenge.isBearer( value ) )
        {
            bearerChallenged = true;
            return BearerChallenge.withResourceMetadata( value, request );
        }
        return value;
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
        pendingStatus = 0;
        if ( !errorHandled && !bearerChallenged && isUnauthorizedError( sc ) && !isErrorAlreadyHandled() && isInteractiveLoginEnabled() )
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

    // handle401 targets the default id provider: the first one of the vhost.
    private boolean isInteractiveLoginEnabled()
    {
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( request );
        final Iterator<VirtualHostIdProvider> idProviders = virtualHost.getIdProviders().values().iterator();
        if ( !idProviders.hasNext() )
        {
            return false;
        }
        return idProviders.next().serves( IdProviderFlow.LOGIN );
    }

    private boolean isErrorAlreadyHandled()
    {
        return Boolean.TRUE.equals( request.getAttribute( "error.handled" ) );
    }
}
