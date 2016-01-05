package com.enonic.xp.web.impl.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.auth.AuthService;
import com.enonic.xp.web.auth.AuthServiceRegistry;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;


public class AuthResponseWrapper
    extends HttpServletResponseWrapper
{
    private final HttpServletRequest request;

    private final SecurityService securityService;

    private final AuthServiceRegistry authServiceRegistry;

    private boolean errorHandled;

    public AuthResponseWrapper( final HttpServletRequest request, final HttpServletResponse response, final SecurityService securityService,
                                final AuthServiceRegistry authServiceRegistry )
    {
        super( response );
        this.request = request;
        this.securityService = securityService;
        this.authServiceRegistry = authServiceRegistry;
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
            final AuthService authService = retrieveAuthService();
            if ( authService != null )
            {
                errorHandled = true;
                System.out.println( "Handle error" );
                authService.authenticate( request, (HttpServletResponse) getResponse() );
            }
        }
    }

    private AuthService retrieveAuthService()
    {
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( request );
        if ( virtualHost != null )
        {
            final String userStoreKey = virtualHost.getUserstore();
            if ( userStoreKey != null )
            {
                final UserStore userStore = runAsAuthenticated( () -> securityService.getUserStore( UserStoreKey.from( userStoreKey ) ) );
                if ( userStore != null )
                {
                    final String authServiceKey = userStore.getAuthServiceKey();
                    if ( authServiceKey != null )
                    {
                        return authServiceRegistry.getAuthService( authServiceKey );
                    }
                }
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