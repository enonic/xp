package com.enonic.xp.admin.impl.security;

import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.auth.AuthControllerScript;
import com.enonic.xp.portal.auth.AuthControllerScriptFactory;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.session.Session;

public final class AuthHelper
{
    private final SecurityService securityService;

    private final AuthDescriptorService authDescriptorService;

    private final AuthControllerScriptFactory authControllerScriptFactory;

    public AuthHelper( final SecurityService securityService, final AuthDescriptorService authDescriptorService,
                       final AuthControllerScriptFactory authControllerScriptFactory )
    {
        this.securityService = securityService;
        this.authDescriptorService = authDescriptorService;
        this.authControllerScriptFactory = authControllerScriptFactory;
    }

    public AuthenticationInfo login( final String user, final String password, final UserStoreKey userStoreKey, final boolean rememberMe )
    {
        final AuthenticationInfo info = authenticate( user, password, userStoreKey, rememberMe );

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

    public void logout()
    {
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        if ( session != null )
        {
            final AuthenticationInfo authenticationInfo = session.getAttribute( AuthenticationInfo.class );
            if ( authenticationInfo != null )
            {
                final UserStore userStore = retrieveUserStore( authenticationInfo );
                final AuthDescriptor authDescriptor = retrieveAuthDescriptor( userStore );

                final PortalRequest portalRequest = new PortalRequest();
                portalRequest.setBaseUri( "/portal" );
                portalRequest.setApplicationKey( authDescriptor.getKey() );
                portalRequest.setUserStore( userStore );

                final AuthControllerScript authControllerScript = authControllerScriptFactory.fromScript( authDescriptor.getResourceKey() );
                authControllerScript.execute( "handleLogout", portalRequest );
            }
            session.invalidate();
        }
    }

    private AuthenticationInfo authenticate( final String user, final String password, final UserStoreKey userStoreKey,
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
            authInfo = securityService.authenticate( emailAuthToken );
        }
        if ( authInfo == null || !authInfo.isAuthenticated() )
        {
            final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
            usernameAuthToken.setUsername( user );
            usernameAuthToken.setPassword( password );
            usernameAuthToken.setUserStore( userStoreKey );
            usernameAuthToken.setRememberMe( rememberMe );
            authInfo = securityService.authenticate( usernameAuthToken );
        }
        return authInfo;
    }

    private boolean isValidEmail( final String value )
    {
        return StringUtils.countMatches( value, "@" ) == 1;
    }

    private UserStore retrieveUserStore( final AuthenticationInfo authenticationInfo )
    {
        final User user = authenticationInfo.getUser();
        if ( user != null )
        {
            final UserStoreKey userStoreKey = user.getKey().
                getUserStore();
            if ( userStoreKey != null )
            {
                return runWithAdminRole( () -> securityService.getUserStore( userStoreKey ) );
            }
        }
        return null;
    }

    private AuthDescriptor retrieveAuthDescriptor( final UserStore userStore )
    {
        if ( userStore != null )
        {
            final AuthConfig authConfig = userStore.getAuthConfig();
            if ( authConfig != null )
            {
                return authDescriptorService.getDescriptor( authConfig.getApplicationKey() );
            }
        }
        return null;
    }


    private <T> T runWithAdminRole( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authenticationInfo = AuthenticationInfo.copyOf( context.getAuthInfo() ).
            principals( RoleKeys.ADMIN ).
            build();
        return ContextBuilder.from( context ).
            authInfo( authenticationInfo ).
            build().
            callWith( callable );
    }

}
