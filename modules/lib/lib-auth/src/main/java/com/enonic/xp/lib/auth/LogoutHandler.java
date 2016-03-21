package com.enonic.xp.lib.auth;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.auth.AuthControllerScript;
import com.enonic.xp.portal.auth.AuthControllerScriptFactory;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;

public final class LogoutHandler
    implements ScriptBean
{
    private Supplier<Context> context;

    private Supplier<SecurityService> securityService;

    private Supplier<AuthDescriptorService> authDescriptorService;

    private Supplier<AuthControllerScriptFactory> authControllerScriptFactory;

    public void logout()
    {
        final Session session = this.context.get().getLocalScope().getSession();
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

                final AuthControllerScript authControllerScript = authControllerScriptFactory.get().
                    fromScript( authDescriptor.getResourceKey() );
                authControllerScript.execute( "handleLogout", portalRequest );
            }

            session.invalidate();
        }
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
                return runWithAdminRole( () -> securityService.get().
                    getUserStore( userStoreKey ) );
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
                return authDescriptorService.get().
                    getDescriptor( authConfig.getApplicationKey() );
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

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context.getBinding( Context.class );
        this.securityService = context.getService( SecurityService.class );
        this.authDescriptorService = context.getService( AuthDescriptorService.class );
        this.authControllerScriptFactory = context.getService( AuthControllerScriptFactory.class );
    }
}
