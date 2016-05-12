package com.enonic.xp.portal.impl.auth;


import java.io.IOException;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.auth.AuthControllerScript;
import com.enonic.xp.portal.auth.AuthControllerScriptFactory;
import com.enonic.xp.portal.impl.PortalRequestAdapter;
import com.enonic.xp.portal.impl.serializer.ResponseSerializer;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

public class AuthControllerWorker
{
    private final SecurityService securityService;

    private final AuthControllerScriptFactory authControllerScriptFactory;

    private final AuthDescriptorService authDescriptorService;

    private final HttpServletRequest request;

    public AuthControllerWorker( final SecurityService securityService, final AuthControllerScriptFactory authControllerScriptFactory,
                                 final AuthDescriptorService authDescriptorService, final HttpServletRequest request )
    {
        this.securityService = securityService;
        this.authControllerScriptFactory = authControllerScriptFactory;
        this.authDescriptorService = authDescriptorService;
        this.request = request;
    }

    public boolean execute( final String functionName )
        throws IOException
    {
        return serialize( functionName, null );
    }

    public boolean serialize( final String functionName, final HttpServletResponse response )
        throws IOException
    {
        final UserStore userStore = retrieveUserStore();
        final AuthDescriptor authDescriptor = retrieveAuthDescriptor( userStore );

        if ( authDescriptor != null )
        {

            final AuthControllerScript authControllerScript = authControllerScriptFactory.fromScript( authDescriptor.getResourceKey() );
            if ( authControllerScript.hasMethod( functionName ) )
            {
                final PortalRequest portalRequest = new PortalRequestAdapter().
                    adapt( request );
                portalRequest.setApplicationKey( authDescriptor.getKey() );
                portalRequest.setUserStore( userStore );

                final PortalResponse portalResponse = authControllerScript.execute( functionName, portalRequest );
                if ( response != null )
                {
                    final ResponseSerializer serializer = new ResponseSerializer( portalRequest, portalResponse );
                    serializer.serialize( response );
                }
                return true;
            }
        }

        return false;
    }

    private UserStore retrieveUserStore()
    {
        UserStoreKey userStoreKey = null;
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( request );
        if ( virtualHost != null )
        {
            userStoreKey = virtualHost.getUserStoreKey();
        }
        if ( userStoreKey == null )
        {
            userStoreKey = UserStoreKey.system();
        }
        if ( userStoreKey != null )
        {
            final UserStoreKey finalUserStoreKey = userStoreKey;
            return runWithAdminRole( () -> securityService.getUserStore( finalUserStoreKey ) );
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
