package com.enonic.xp.web.impl.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.enonic.xp.security.SecurityService;


public class AuthResponseWrapper
    extends HttpServletResponseWrapper
{
//    private final HttpServletRequest request;
//
//    private final SecurityService securityService;

    private boolean errorHandled;

    public AuthResponseWrapper( final HttpServletRequest request, final HttpServletResponse response,
                                final SecurityService securityService )
    {
        super( response );
//        this.request = request;
//        this.securityService = securityService;
    }

    @Override
    public void setStatus( final int sc )
    {
        if ( canHandleError( sc ) )
        {
            handleError();
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
        if ( canHandleError( sc ) )
        {
            handleError();
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
        if ( canHandleError( sc ) )
        {
            handleError();
        }
        else
        {
            super.sendError( sc, msg );
        }
    }

    private boolean canHandleError( final int sc )
    {
        return 403 == sc || 401 == sc;
    }


    private void handleError()
    {
        errorHandled = true;
        System.out.println( "Handle error" );
    }

//    private void handleError()
//        throws UnsupportedEncodingException
//    {
//        final VirtualHost vhost = VirtualHostHelper.getVirtualHost( request );
//        if ( vhost != null )
//        {
//            final UserStoreKey userStoreKey = UserStoreKey.from( vhost.getUserstore() );
//            final UserStore userStore = runAsAuthenticated( () -> securityService.getUserStore( userStoreKey ) );
//            System.out.println( userStore );
//
//            super.setStatus( 303 );
//            super.setHeader( "Location", "/123" );
//            redirected = true;
//        }
//    }
//
//
//    private <T> T runAsAuthenticated( final Callable<T> callable )
//    {
//        final Context context = ContextAccessor.current();
//        final AuthenticationInfo authenticationInfo = AuthenticationInfo.copyOf( context.getAuthInfo() ).
//            principals( RoleKeys.AUTHENTICATED ).
//            build();
//        return ContextBuilder.from( context ).
//            authInfo( authenticationInfo ).
//            build().
//            callWith( callable );
//}
}