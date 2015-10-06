package com.enonic.xp.lib.auth;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.session.Session;

public final class LoginHandler
    implements ScriptBean
{
    private String user;

    private String password;

    private String[] userStore;

    private Supplier<SecurityService> securityService;

    public void setUser( final String user )
    {
        this.user = user;
    }

    public void setPassword( final String password )
    {
        this.password = password;
    }

    public void setUserStore( final String[] userStore )
    {
        this.userStore = userStore;
    }

    public LoginResultMapper login()
    {
        AuthenticationInfo authInfo = noUserStoreSpecified() ? attemptLoginWithAllExistingUserStores() : attemptLogin();

        if ( authInfo.isAuthenticated() )
        {
            final Session session = ContextAccessor.current().getLocalScope().getSession();
            if ( session != null )
            {
                session.setAttribute( authInfo );
            }

            return new LoginResultMapper( authInfo );
        }
        else
        {
            return new LoginResultMapper( authInfo, "Access Denied" );
        }
    }

    private boolean noUserStoreSpecified()
    {
        return this.userStore == null || this.userStore.length == 0;
    }

    private AuthenticationInfo attemptLoginWithAllExistingUserStores()
    {
        final UserStores userStores = runAsAuthenticated( securityService.get()::getUserStores );

        for ( UserStore userStore : userStores )
        {
            final AuthenticationInfo authInfo = authenticate( userStore.getKey() );
            if ( ( authInfo != null ) && ( authInfo.isAuthenticated() ) )
            {
                return authInfo;
            }
        }

        return AuthenticationInfo.unAuthenticated();
    }

    private AuthenticationInfo attemptLogin()
    {

        for ( String uStore : userStore )
        {
            final AuthenticationInfo authInfo = authenticate( UserStoreKey.from( uStore ) );
            if ( ( authInfo != null ) && ( authInfo.isAuthenticated() ) )
            {
                return authInfo;
            }
        }

        return AuthenticationInfo.unAuthenticated();
    }

    private AuthenticationInfo authenticate( UserStoreKey userStore )
    {
        AuthenticationInfo authInfo = null;

        if ( isValidEmail( this.user ) )
        {
            final EmailPasswordAuthToken emailAuthToken = new EmailPasswordAuthToken();
            emailAuthToken.setEmail( this.user );
            emailAuthToken.setPassword( this.password );
            emailAuthToken.setUserStore( userStore );

            authInfo = runAsAuthenticated( () -> this.securityService.get().authenticate( emailAuthToken ) );
        }

        if ( authInfo == null || !authInfo.isAuthenticated() )
        {
            final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
            usernameAuthToken.setUsername( this.user );
            usernameAuthToken.setPassword( this.password );
            usernameAuthToken.setUserStore( userStore );

            authInfo = runAsAuthenticated( () -> this.securityService.get().authenticate( usernameAuthToken ) );
        }

        return authInfo;
    }

    private <T> T runAsAuthenticated( Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( authInfo ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SECURITY ).build().
            callWith( runnable );
    }

    private boolean isValidEmail( final String value )
    {
        return StringUtils.countMatches( value, "@" ) == 1;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
