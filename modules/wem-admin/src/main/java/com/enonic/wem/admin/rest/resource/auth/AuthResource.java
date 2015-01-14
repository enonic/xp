package com.enonic.wem.admin.rest.resource.auth;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.EmailPasswordAuthToken;
import com.enonic.wem.api.security.auth.UsernamePasswordAuthToken;
import com.enonic.wem.api.session.Session;

@Path(ResourceConstants.REST_ROOT + "auth")
@Produces(MediaType.APPLICATION_JSON)
public final class AuthResource
    implements AdminResource
{
    private SecurityService securityService;

    @POST
    @Path("login")
    public LoginResultJson login( final LoginJson login )
    {
        final String user = login.getUser();
        final AuthenticationInfo authInfo;
        if ( StringUtils.countMatches( user, "\\" ) == 1 )
        {
            final String[] userParts = user.split( "\\\\" );
            final String userStore = userParts[0];
            final String userName = userParts[1];
            final UserStoreKey userStoreKey = new UserStoreKey( userStore );

            final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
            usernameAuthToken.setUsername( userName );
            usernameAuthToken.setPassword( login.getPassword() );
            usernameAuthToken.setUserStore( userStoreKey );
            usernameAuthToken.setRememberMe( login.isRememberMe() );

            authInfo = securityService.authenticate( usernameAuthToken );
        }
        else
        {
            authInfo = doLogin( user, login.getPassword(), login.isRememberMe() );
        }

        if ( authInfo.isAuthenticated() )
        {
            if ( !authInfo.hasRole( RoleKeys.ADMIN_LOGIN ) )
            {
                logout();
                return new LoginResultJson( AuthenticationInfo.unAuthenticated() );
            }

            final Session session = ContextAccessor.current().getLocalScope().getSession();
            if ( session != null )
            {
                session.setAttribute( authInfo );
            }
        }

        return new LoginResultJson( authInfo );
    }

    @POST
    @Path("logout")
    public void logout()
    {
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        if ( session != null )
        {
            session.invalidate();
        }
    }

    @GET
    @Path("authenticated")
    public LoginResultJson isAuthenticated()
    {
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        if ( session == null )
        {
            return new LoginResultJson( AuthenticationInfo.unAuthenticated() );
        }

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return new LoginResultJson( authInfo );
    }

    private AuthenticationInfo doLogin( final String user, final String password, final boolean rememberMe )
    {
        final UserStores userStores = securityService.getUserStores();
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

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
