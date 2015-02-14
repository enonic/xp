package com.enonic.xp.admin.impl.security;

import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
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
            if ( !info.hasRole( RoleKeys.ADMIN_LOGIN ) )
            {
                logout();
                return AuthenticationInfo.unAuthenticated();
            }

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
        if ( StringUtils.countMatches( user, "\\" ) == 1 )
        {
            final String[] userParts = user.split( "\\\\" );
            final String userStore = userParts[0];
            final String userName = userParts[1];
            final UserStoreKey userStoreKey = new UserStoreKey( userStore );

            final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
            usernameAuthToken.setUsername( userName );
            usernameAuthToken.setPassword( password );
            usernameAuthToken.setUserStore( userStoreKey );
            usernameAuthToken.setRememberMe( rememberMe );

            return authenticate( usernameAuthToken );
        }
        else
        {
            return doLogin( user, password, rememberMe );
        }
    }

    private AuthenticationInfo doLogin( final String user, final String password, final boolean rememberMe )
    {
        final UserStores userStores = runAsAuthenticated( securityService::getUserStores );
        for ( UserStore userStore : userStores )
        {
            final AuthenticationInfo authInfo = loginWithUserStore( user, password, userStore.getKey(), rememberMe );
            if ( ( authInfo != null ) && ( authInfo.isAuthenticated() ) )
            {
                return authInfo;
            }
        }
        return AuthenticationInfo.unAuthenticated();
    }

    private AuthenticationInfo loginWithUserStore( final String user, final String password, final UserStoreKey userStoreKey,
                                                   final boolean rememberMe )
    {
        AuthenticationInfo authInfo = null;
        if ( isValidEmail( user ) )
        {
            final EmailPasswordAuthToken emailAuthToken = new EmailPasswordAuthToken();
            emailAuthToken.setEmail( user );
            emailAuthToken.setPassword( password );
            emailAuthToken.setUserStore( userStoreKey );
            emailAuthToken.setRememberMe( rememberMe );

            authInfo = authenticate( emailAuthToken );
        }
        if ( authInfo == null || !authInfo.isAuthenticated() )
        {
            final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
            usernameAuthToken.setUsername( user );
            usernameAuthToken.setPassword( password );
            usernameAuthToken.setUserStore( userStoreKey );
            usernameAuthToken.setRememberMe( rememberMe );

            authInfo = authenticate( usernameAuthToken );
        }

        return authInfo;
    }

    private boolean isValidEmail( final String value )
    {
        return StringUtils.countMatches( value, "@" ) == 1;
    }

    private AuthenticationInfo authenticate( AuthenticationToken token )
    {
        return runAsAuthenticated( () -> securityService.authenticate( token ) );
    }

    private <T> T runAsAuthenticated( Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( ContextAccessor.current() ).authInfo( authInfo ).build().callWith( runnable );
    }
}
