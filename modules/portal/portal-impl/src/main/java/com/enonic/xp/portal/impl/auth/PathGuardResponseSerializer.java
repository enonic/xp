package com.enonic.xp.portal.impl.auth;


import java.io.IOException;
import java.util.Optional;
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
import com.enonic.xp.security.PathGuard;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class PathGuardResponseSerializer
{
    private final SecurityService securityService;

    private final AuthControllerScriptFactory authControllerScriptFactory;

    private final AuthDescriptorService authDescriptorService;

    private final HttpServletRequest request;

    public PathGuardResponseSerializer( final SecurityService securityService,
                                        final AuthControllerScriptFactory authControllerScriptFactory,
                                        final AuthDescriptorService authDescriptorService, final HttpServletRequest request )
    {
        this.securityService = securityService;
        this.authControllerScriptFactory = authControllerScriptFactory;
        this.authDescriptorService = authDescriptorService;
        this.request = request;
    }

    public boolean serialize( final HttpServletResponse response )
        throws IOException
    {
        final PathGuard pathGuard = retrievePathGuard();
        final UserStore userStore = retrieveUserStore( pathGuard );
        final AuthDescriptor authDescriptor = retrieveAuthDescriptor( userStore );

        if ( authDescriptor != null )
        {

            final PortalRequest portalRequest = new PortalRequestAdapter().
                adapt( request );
            portalRequest.setApplicationKey( authDescriptor.getKey() );
            portalRequest.setUserStore( userStore );

            final AuthControllerScript authControllerScript = authControllerScriptFactory.fromScript( authDescriptor.getResourceKey() );
            final PortalResponse portalResponse = authControllerScript.execute( "login", portalRequest );

            final ResponseSerializer serializer = new ResponseSerializer( portalRequest, portalResponse );
            serializer.serialize( response );
            return true;
        }

        return false;
    }

    private PathGuard retrievePathGuard()
    {
        //Retrieves the PathGuard for the current path
        final String requestURI = request.getRequestURI();
        final Optional<PathGuard> pathGuardOptional = runWithAdminRole( () -> securityService.getPathGuardByPath( requestURI ) );
        return pathGuardOptional.orElse( null );
    }

    private UserStore retrieveUserStore( PathGuard pathGuard )
    {
        UserStoreKey userStoreKey = pathGuard == null ? null : pathGuard.getUserStoreKey();
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
