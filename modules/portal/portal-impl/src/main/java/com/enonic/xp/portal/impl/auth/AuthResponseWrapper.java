package com.enonic.xp.portal.impl.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAdapter;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.error.ErrorHandlerScript;
import com.enonic.xp.portal.impl.error.ErrorHandlerScriptFactory;
import com.enonic.xp.portal.impl.error.PortalError;
import com.enonic.xp.portal.impl.serializer.ResponseSerializer;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreAuthConfig;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.Exceptions;
import com.enonic.xp.web.HttpStatus;


public class AuthResponseWrapper
    extends HttpServletResponseWrapper
{
    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final SecurityService securityService;

    private final AuthDescriptorService authDescriptorService;

    private final ErrorHandlerScriptFactory errorHandlerScriptFactory;

    private final UserStoreKey userStoreKey;

    private boolean errorHandled;

    public AuthResponseWrapper( final HttpServletRequest request, final HttpServletResponse response, final SecurityService securityService,
                                final AuthDescriptorService authDescriptorService,
                                final ErrorHandlerScriptFactory errorHandlerScriptFactory, final String userStoreKey )
    {
        super( response );
        this.request = request;
        this.response = response;
        this.securityService = securityService;
        this.authDescriptorService = authDescriptorService;
        this.errorHandlerScriptFactory = errorHandlerScriptFactory;
        this.userStoreKey = UserStoreKey.from( userStoreKey );
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
        if ( 403 == sc || 401 == sc )
        {
            final AuthDescriptor authDescriptor = retrieveAuthDescriptor();
            if ( authDescriptor != null )
            {
                try
                {
                    final PortalRequest portalRequest = new PortalRequestAdapter().
                        adapt( request );
                    portalRequest.setBaseUri( "/portal" );
                    portalRequest.setApplicationKey( authDescriptor.getKey() );

                    final PortalError portalError = PortalError.create().
                        status( HttpStatus.FORBIDDEN ).
                        message( "Forbidden" ).
                        request( portalRequest ).
                        build();

                    final ErrorHandlerScript errorHandlerScript = errorHandlerScriptFactory.errorScript( authDescriptor.getResourceKey() );
                    final PortalResponse portalResponse = errorHandlerScript.execute( portalError );

                    final ResponseSerializer serializer = new ResponseSerializer( portalRequest, portalResponse );

                    serializer.serialize( response );
                    errorHandled = true;
                }
                catch ( IOException e )
                {
                    throw Exceptions.unchecked( e );
                }
            }
        }
    }

    private AuthDescriptor retrieveAuthDescriptor()
    {
        final UserStore userStore = runAsAuthenticated( () -> securityService.getUserStore( userStoreKey ) );
        if ( userStore != null )
        {
            final UserStoreAuthConfig authConfig = userStore.getAuthConfig();
            if ( authConfig != null )
            {
                return authDescriptorService.getDescriptor( authConfig.getApplicationKey() );
            }
        }
        return null;
    }

    private <T> T runAsAuthenticated( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authenticationInfo = AuthenticationInfo.copyOf( context.getAuthInfo() ).
            principals( RoleKeys.AUTHENTICATED ).
            build();
        return ContextBuilder.from( context ).
            authInfo( authenticationInfo ).
            build().
            callWith( callable );
    }
}