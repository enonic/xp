package com.enonic.xp.admin.impl.security;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.session.Session;

public final class AuthHelper
{
    private final SecurityService securityService;

    public AuthHelper( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public AuthenticationInfo login( final String user, final String password, final boolean rememberMe )
    {
        final AuthenticationInfo info = authenticate( user, password, rememberMe );

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

    public static void logout()
    {
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        if ( session != null )
        {
            session.invalidate();
        }
    }

    private AuthenticationInfo authenticate( final String user, final String password, final boolean rememberMe )
    {
        AuthenticationInfo authInfo = null;
        if ( isValidEmail( user ) )
        {
            final EmailPasswordAuthToken emailAuthToken = new EmailPasswordAuthToken();
            emailAuthToken.setEmail( user );
            emailAuthToken.setPassword( password );
            emailAuthToken.setRememberMe( rememberMe );
            authInfo = securityService.authenticate( emailAuthToken );
        }
        if ( authInfo == null || !authInfo.isAuthenticated() )
        {
            final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
            usernameAuthToken.setUsername( user );
            usernameAuthToken.setPassword( password );
            usernameAuthToken.setRememberMe( rememberMe );
            authInfo = securityService.authenticate( usernameAuthToken );
        }
        return authInfo;
    }

    private boolean isValidEmail( final String value )
    {
        return StringUtils.countMatches( value, "@" ) == 1;
    }

}
