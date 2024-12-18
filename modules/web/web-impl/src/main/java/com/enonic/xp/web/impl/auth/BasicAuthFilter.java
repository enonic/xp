package com.enonic.xp.web.impl.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.session.Session;
import com.enonic.xp.web.filter.OncePerRequestFilter;

@Component(immediate = true, service = Filter.class, property = {"connector=api"})
@Order(-40)
@WebFilter("/*")
public final class BasicAuthFilter
    extends OncePerRequestFilter
{
    private final SecurityService securityService;

    @Activate
    public BasicAuthFilter( @Reference final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        login( req );
        chain.doFilter( req, res );
    }

    private void login( final HttpServletRequest req )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            return;
        }

        final String header = req.getHeader( HttpHeaders.AUTHORIZATION );
        if ( header == null )
        {
            return;
        }

        final String[] parts = parseHeader( header );
        if ( parts == null )
        {
            return;
        }

        final AuthenticationInfo info = authenticate( parts[0], parts[1] );
        if ( info.isAuthenticated() )
        {
            createSession( info );
        }
    }

    private static String[] parseHeader( final String header )
    {
        if ( header.length() < 6 )
        {
            return null;
        }

        final String type = header.substring( 0, 5 );
        if ( !type.equalsIgnoreCase( HttpServletRequest.BASIC_AUTH ) )
        {
            return null;
        }

        final String val = header.substring( 6 );

        final String decoded = new String( Base64.getDecoder().decode( val ), StandardCharsets.UTF_8 );

        int pos = decoded.indexOf( ':' );
        if ( pos == -1 )
        {
            return null;
        }

        return new String[]{decoded.substring( 0, pos ), decoded.substring( pos + 1 )};
    }

    private AuthenticationInfo authenticate( final String user, final String password )
    {
        AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();

        if ( isValidEmail( user ) )
        {
            final EmailPasswordAuthToken emailAuthToken = new EmailPasswordAuthToken();
            emailAuthToken.setEmail( user );
            emailAuthToken.setPassword( password );
            authInfo = securityService.authenticate( emailAuthToken );
        }
        if ( !authInfo.isAuthenticated() )
        {
            final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
            usernameAuthToken.setUsername( user );
            usernameAuthToken.setPassword( password );
            authInfo = securityService.authenticate( usernameAuthToken );
        }
        return authInfo;
    }

    private boolean isValidEmail( final String value )
    {
        return value != null && value.chars().filter( ch -> ch == '@' ).count() == 1;
    }

    private void createSession( final AuthenticationInfo authInfo )
    {
        final LocalScope localScope = ContextAccessor.current().getLocalScope();
        final Session session = localScope.getSession();

        if ( session != null )
        {
            final var attributes = session.getAttributes();
            session.invalidate();

            final Session newSession = localScope.getSession();

            if ( newSession != null )
            {
                attributes.forEach( newSession::setAttribute );
                session.setAttribute( authInfo );
            }
        }
    }
}
