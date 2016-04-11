package com.enonic.xp.portal.impl.handler.identity;

import java.util.concurrent.Callable;

import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.auth.AuthControllerScript;
import com.enonic.xp.portal.auth.AuthControllerScriptFactory;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

final class IdentityHandlerWorker
    extends ControllerHandlerWorker
{
    protected UserStoreKey userStoreKey;

    protected SecurityService securityService;

    protected AuthDescriptorService authDescriptorService;

    protected AuthControllerScriptFactory authControllerScriptFactory;

    protected String idProviderFunction;

    @Override
    public void execute()
        throws Exception
    {
        PortalResponse portalResponse = null;
        final UserStore userStore = retrieveUserStore();
        final AuthDescriptor authDescriptor = retrieveAuthDescriptor( userStore );
        if ( authDescriptor != null )
        {
            this.request.setApplicationKey( authDescriptor.getKey() );
            this.request.setUserStore( userStore );

            final AuthControllerScript authControllerScript = authControllerScriptFactory.fromScript( authDescriptor.getResourceKey() );
            portalResponse = authControllerScript.execute( idProviderFunction, this.request );
        }

        if ( portalResponse == null )
        {
            throw notFound( "ID Provider function [%s] not found for user store [%s]", idProviderFunction, userStoreKey );
        }
        else
        {
            this.response = PortalResponse.create( portalResponse );
        }
    }


    private UserStore retrieveUserStore()
    {
        if ( userStoreKey != null )
        {
            return runWithAdminRole( () -> securityService.getUserStore( userStoreKey ) );
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
