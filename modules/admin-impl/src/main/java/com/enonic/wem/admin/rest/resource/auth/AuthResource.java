package com.enonic.wem.admin.rest.resource.auth;

import java.util.concurrent.Callable;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.AuthenticationToken;
import com.enonic.wem.api.security.auth.EmailPasswordAuthToken;
import com.enonic.wem.api.security.auth.UsernamePasswordAuthToken;
import com.enonic.wem.api.session.Session;

@Path(ResourceConstants.REST_ROOT + "auth")
@Produces(MediaType.APPLICATION_JSON)
@Component(immediate = true)
public final class AuthResource
    implements AdminResource
{

    private final AdminApplicationsRegistry appRegistry;

    private SecurityService securityService;

    public AuthResource()
    {
        this.appRegistry = new AdminApplicationsRegistry();
    }

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

            authInfo = authenticate( usernameAuthToken );
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

        return new LoginResultJson( authInfo, appRegistry.getAllowedApplications( authInfo.getPrincipals() ) );
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
        return new LoginResultJson( authInfo, appRegistry.getAllowedApplications( authInfo.getPrincipals() ) );
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

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
