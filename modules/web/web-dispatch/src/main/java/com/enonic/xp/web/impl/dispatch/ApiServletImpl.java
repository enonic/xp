package com.enonic.xp.web.impl.dispatch;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.session.Session;
import com.enonic.xp.web.dispatch.ApiServlet;
import com.enonic.xp.web.servlet.ServletRequestHolder;

@Component(immediate = true, service = ApiServlet.class)
public final class ApiServletImpl
    extends HttpServlet
    implements ApiServlet
{
    private Servlet servlet;

    private SecurityService securityService;

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        ServletRequestHolder.setRequest( req );
        try
        {
            final AuthenticationInfo authInfo = this.login( req );
            final Context ctx = ContextBuilder.from( ContextAccessor.current() ).
                authInfo( authInfo ).
                build();

            ctx.callWith( () -> {
                this.servlet.service( new HttpRequestDelegate( req ), res );
                return null;
            } );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }
    }

    private AuthenticationInfo login( final HttpServletRequest req )
    {
        final String header = req.getHeader( HttpHeaders.AUTHORIZATION );
        if ( header == null )
        {
            return AuthenticationInfo.unAuthenticated();
        }

        final String[] parts = parseHeader( header );
        if ( parts == null )
        {
            return AuthenticationInfo.unAuthenticated();
        }

        final String user = parts[0];
        final String password = parts[1];

        final AuthenticationInfo info = authenticate( user, password );

        if ( info.isAuthenticated() )
        {
            final Session session = ContextAccessor.current().getLocalScope().getSession();
            if ( session != null )
            {
                session.setAttribute( info );
            }
        }

        return info;
    }

    private AuthenticationInfo authenticate( final String user, final String password )
    {
        final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
        usernameAuthToken.setUsername( user );
        usernameAuthToken.setPassword( password );
        return securityService.authenticate( usernameAuthToken );
    }

    private static String[] parseHeader( final String header )
    {
        if ( header.length() < 6 )
        {
            return null;
        }

        final String type = header.substring( 0, 5 ).toUpperCase();
        if ( !type.equals( HttpServletRequest.BASIC_AUTH ) )
        {
            return null;
        }

        final String val = header.substring( 6 );
        final BaseEncoding encoding = BaseEncoding.base64();

        final String decoded = new String( encoding.decode( val ), Charsets.UTF_8 );
        final String[] parts = decoded.split( ":" );

        if ( parts.length != 2 )
        {
            return null;
        }

        return parts;
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Override
    public void setServlet( final Servlet servlet )
    {
        this.servlet = servlet;
    }
}
