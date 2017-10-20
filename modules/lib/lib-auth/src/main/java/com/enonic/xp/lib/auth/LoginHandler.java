package com.enonic.xp.lib.auth;

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.security.auth.VerifiedEmailAuthToken;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;
import com.enonic.xp.session.Session;

public final class LoginHandler
    implements ScriptBean
{
    private String user;

    private String password;

    private boolean skipAuth;

    private String[] userStore;

    private Integer sessionTimeout;

    private Supplier<SecurityService> securityService;

    private Supplier<Context> context;

    private Supplier<PortalRequest> portalRequestSupplier;

    public void setUser( final String user )
    {
        this.user = user;
    }

    public void setPassword( final String password )
    {
        this.password = password;
    }

    public void setSkipAuth( final boolean skipAuth )
    {
        this.skipAuth = skipAuth;
    }

    public void setUserStore( final String[] userStore )
    {
        this.userStore = userStore;
    }

    public void setSessionTimeout( final Integer sessionTimeout )
    {
        this.sessionTimeout = sessionTimeout;
    }

    public LoginResultMapper login()
    {
        AuthenticationInfo authInfo = noUserStoreSpecified() ? attemptLoginWithAllExistingUserStores() : attemptLogin();

        if ( authInfo.isAuthenticated() )
        {
            final Session session = this.context.get().getLocalScope().getSession();
            if ( session != null )
            {
                session.setAttribute( authInfo );
            }

            if ( this.sessionTimeout != null )
            {
                setSessionTimeout();
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
        final UserStores userStores = runAsAuthenticated( this::getSortedUserStores );

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

    private UserStores getSortedUserStores()
    {
        UserStores userStores = securityService.get().getUserStores();
        return UserStores.from( userStores.stream().
            sorted( Comparator.comparing( u -> u.getKey().toString() ) ).
            collect( Collectors.toList() ) );
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
            if ( this.skipAuth )
            {
                final VerifiedEmailAuthToken verifiedEmailAuthToken = new VerifiedEmailAuthToken();
                verifiedEmailAuthToken.setEmail( this.user );
                verifiedEmailAuthToken.setUserStore( userStore );

                authInfo = runAsAuthenticated( () -> this.securityService.get().authenticate( verifiedEmailAuthToken ) );
            }
            else
            {
                final EmailPasswordAuthToken emailAuthToken = new EmailPasswordAuthToken();
                emailAuthToken.setEmail( this.user );
                emailAuthToken.setPassword( this.password );
                emailAuthToken.setUserStore( userStore );

                authInfo = runAsAuthenticated( () -> this.securityService.get().authenticate( emailAuthToken ) );
            }
        }

        if ( authInfo == null || !authInfo.isAuthenticated() )
        {
            if ( this.skipAuth )
            {
                final VerifiedUsernameAuthToken usernameAuthToken = new VerifiedUsernameAuthToken();
                usernameAuthToken.setUsername( this.user );
                usernameAuthToken.setUserStore( userStore );

                authInfo = runAsAuthenticated( () -> this.securityService.get().authenticate( usernameAuthToken ) );
            }
            else
            {
                final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
                usernameAuthToken.setUsername( this.user );
                usernameAuthToken.setPassword( this.password );
                usernameAuthToken.setUserStore( userStore );

                authInfo = runAsAuthenticated( () -> this.securityService.get().authenticate( usernameAuthToken ) );
            }
        }

        return authInfo;
    }

    private <T> T runAsAuthenticated( Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( this.context.get() ).
            authInfo( authInfo ).
            repositoryId( SecurityConstants.SECURITY_REPO.getId() ).
            branch( SecurityConstants.BRANCH_SECURITY ).build().
            callWith( runnable );
    }

    private boolean isValidEmail( final String value )
    {
        return StringUtils.countMatches( value, "@" ) == 1;
    }

    private void setSessionTimeout()
    {
        final PortalRequest portalRequest = this.portalRequestSupplier.get();
        if ( portalRequest != null )
        {
            final HttpSession httpSession = portalRequest.getRawRequest().getSession();
            if ( httpSession != null )
            {
                httpSession.setMaxInactiveInterval( this.sessionTimeout );
            }
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
        this.context = context.getBinding( Context.class );
        this.portalRequestSupplier = context.getBinding( PortalRequest.class );
    }
}
