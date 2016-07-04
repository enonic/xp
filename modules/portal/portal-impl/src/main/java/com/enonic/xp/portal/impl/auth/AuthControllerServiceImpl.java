package com.enonic.xp.portal.impl.auth;


import java.io.IOException;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.auth.AuthControllerExecutionParams;
import com.enonic.xp.portal.auth.AuthControllerService;
import com.enonic.xp.portal.impl.PortalRequestAdapter;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.serializer.ResponseSerializationService;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Component
public class AuthControllerServiceImpl
    implements AuthControllerService
{
    private AuthControllerScriptFactory authControllerScriptFactory;

    private AuthDescriptorService authDescriptorService;

    private SecurityService securityService;

    private ResponseSerializationService responseSerializationService;

    @Override
    public PortalResponse execute( final AuthControllerExecutionParams params )
        throws IOException
    {
        final UserStoreKey userStoreKey = retrieveUserStoreKey( params );
        final UserStore userStore = retrieveUserStore( userStoreKey );
        final AuthDescriptor authDescriptor = retrieveAuthDescriptor( userStore );

        if ( authDescriptor != null )
        {
            final AuthControllerScript authControllerScript = authControllerScriptFactory.fromScript( authDescriptor.getResourceKey() );
            final String functionName = params.getFunctionName();
            if ( authControllerScript.hasMethod( functionName ) )
            {
                PortalRequest portalRequest = params.getPortalRequest();
                if ( portalRequest == null )
                {
                    portalRequest = new PortalRequestAdapter().
                        adapt( params.getServletRequest() );
                }
                portalRequest.setApplicationKey( authDescriptor.getKey() );
                portalRequest.setUserStore( userStore );

                final PortalResponse portalResponse = authControllerScript.execute( functionName, portalRequest );

                if ( portalResponse != null )
                {
                    final HttpServletResponse response = params.getResponse();
                    if ( response != null )
                    {
                        responseSerializationService.serialize( portalRequest, portalResponse, response );
                    }
                }
                return portalResponse;
            }
        }

        return null;
    }

    private UserStoreKey retrieveUserStoreKey( AuthControllerExecutionParams params )
    {
        UserStoreKey userStoreKey = params.getUserStoreKey();
        if ( userStoreKey == null )
        {
            final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( params.getServletRequest() );
            if ( virtualHost != null )
            {
                userStoreKey = virtualHost.getUserStoreKey();
            }
        }
        return userStoreKey;
    }

    private UserStore retrieveUserStore( final UserStoreKey userStoreKey )
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

    @Reference
    public void setAuthControllerScriptFactory( final AuthControllerScriptFactory authControllerScriptFactory )
    {
        this.authControllerScriptFactory = authControllerScriptFactory;
    }

    @Reference
    public void setAuthDescriptorService( final AuthDescriptorService authDescriptorService )
    {
        this.authDescriptorService = authDescriptorService;
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setResponseSerializationService( final ResponseSerializationService responseSerializationService )
    {
        this.responseSerializationService = responseSerializationService;
    }
}
