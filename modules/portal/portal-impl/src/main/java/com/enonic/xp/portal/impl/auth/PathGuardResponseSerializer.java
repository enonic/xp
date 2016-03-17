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
import com.enonic.xp.portal.PortalRequestAdapter;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.error.ErrorHandlerScript;
import com.enonic.xp.portal.impl.error.ErrorHandlerScriptFactory;
import com.enonic.xp.portal.impl.error.PortalError;
import com.enonic.xp.portal.impl.serializer.ResponseSerializer;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.PathGuard;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.HttpStatus;

public class PathGuardResponseSerializer
{
    private final HttpServletRequest request;

    private final SecurityService securityService;

    private final ErrorHandlerScriptFactory errorHandlerScriptFactory;

    private final AuthDescriptorService authDescriptorService;

    private final PathGuard pathGuard;

    public PathGuardResponseSerializer( final HttpServletRequest request, final SecurityService securityService,
                                        final ErrorHandlerScriptFactory errorHandlerScriptFactory,
                                        final AuthDescriptorService authDescriptorService, final PathGuard pathGuard )
    {
        this.request = request;
        this.securityService = securityService;
        this.errorHandlerScriptFactory = errorHandlerScriptFactory;
        this.authDescriptorService = authDescriptorService;
        this.pathGuard = pathGuard;
    }

    public boolean serialize( final HttpServletResponse response )
        throws IOException
    {
        final UserStore userStore = retrieveUserStore();
        final AuthConfig authConfig = userStore == null ? null : userStore.getAuthConfig();
        final AuthDescriptor authDescriptor = retrieveAuthDescriptor( authConfig );

        if ( authDescriptor != null )
        {

            final PortalRequest portalRequest = new PortalRequestAdapter().
                adapt( request );
            portalRequest.setBaseUri( "/portal" );
            portalRequest.setApplicationKey( authDescriptor.getKey() );
            portalRequest.setAuthConfig( authConfig );

            final PortalError portalError = PortalError.create().
                status( HttpStatus.FORBIDDEN ).
                message( "Forbidden" ).
                request( portalRequest ).
                build();

            final ErrorHandlerScript errorHandlerScript = errorHandlerScriptFactory.errorScript( authDescriptor.getResourceKey() );
            final PortalResponse portalResponse = errorHandlerScript.execute( portalError );

            final ResponseSerializer serializer = new ResponseSerializer( portalRequest, portalResponse );

            serializer.serialize( response );
            return true;
        }

        return false;
    }

    private UserStore retrieveUserStore()
    {
        UserStoreKey userStoreKey = pathGuard == null ? null : pathGuard.getUserStoreKey();
        if ( userStoreKey != null )
        {
            return runWithAdminRole( () -> securityService.getUserStore( userStoreKey ) );
        }
        return null;
    }

    private AuthDescriptor retrieveAuthDescriptor( final AuthConfig authConfig )
    {
        if ( authConfig != null )
        {
            return authDescriptorService.getDescriptor( authConfig.getApplicationKey() );
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
