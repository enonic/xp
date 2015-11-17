package com.enonic.xp.lib.security;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.context.Context;
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
import com.enonic.xp.security.auth.VerifiedEmailAuthToken;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;

public class RunWithHandler
    implements ScriptBean
{
    private Supplier<Context> context;

    private Supplier<SecurityService> securityService;

    private String branch;

    private String user;

    public void setBranch( String branch )
    {
        this.branch = branch;
    }

    public void setUser( String user )
    {
        this.user = user;
    }

    public void run( Runnable runnable )
    {
        final ContextBuilder contextBuilder = ContextBuilder.from( context.get() );
        if ( user != null )
        {
            final AuthenticationInfo authInfo = getAuthenticationInfo();
            ContextBuilder.from( context.get() ).
                authInfo( authInfo ).
                build().
                runWith( runnable );
        }

    }

    private AuthenticationInfo getAuthenticationInfo()
    {
        final UserStores userStores = runAsAuthenticated( securityService.get()::getUserStores );

        for ( UserStore userStore : userStores )
        {
            final AuthenticationInfo authInfo = getAuthenticationInfo( userStore.getKey() );
            if ( ( authInfo != null ) && ( authInfo.isAuthenticated() ) )
            {
                return authInfo;
            }
        }

        return AuthenticationInfo.unAuthenticated();
    }

    private AuthenticationInfo getAuthenticationInfo( UserStoreKey userStore )
    {
        AuthenticationInfo authInfo = null;

        if ( isValidEmail( this.user ) )
        {
            final VerifiedEmailAuthToken emailAuthToken = new VerifiedEmailAuthToken();
            emailAuthToken.setEmail( this.user );
            emailAuthToken.setUserStore( userStore );

            authInfo = runAsAuthenticated( () -> this.securityService.get().authenticate( emailAuthToken ) );
        }

        if ( authInfo == null || !authInfo.isAuthenticated() )
        {
            final VerifiedUsernameAuthToken usernameAuthToken = new VerifiedUsernameAuthToken();
            usernameAuthToken.setUsername( this.user );
            usernameAuthToken.setUserStore( userStore );

            authInfo = runAsAuthenticated( () -> this.securityService.get().authenticate( usernameAuthToken ) );
        }

        return authInfo;
    }

    private boolean isValidEmail( final String value )
    {
        return StringUtils.countMatches( value, "@" ) == 1;
    }

    private <T> T runAsAuthenticated( Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( this.context.get() ).
            authInfo( authInfo ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SECURITY ).build().
            callWith( runnable );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context.getBinding( Context.class );
        this.securityService = context.getService( SecurityService.class );
    }
}
