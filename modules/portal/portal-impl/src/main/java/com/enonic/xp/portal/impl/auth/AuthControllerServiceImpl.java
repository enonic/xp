package com.enonic.xp.portal.impl.auth;


import java.io.IOException;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.auth.IdProviderDescriptor;
import com.enonic.xp.auth.IdProviderDescriptorService;
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

    private IdProviderDescriptorService idProviderDescriptorService;

    private SecurityService securityService;

    private ResponseSerializationService responseSerializationService;

    @Override
    public PortalResponse execute( final AuthControllerExecutionParams params )
        throws IOException
    {
        final UserStoreKey userStoreKey = retrieveUserStoreKey( params );
        final UserStore userStore = retrieveUserStore( userStoreKey );
        final IdProviderDescriptor idProviderDescriptor = retrieveIdProviderDescriptor( userStore );

        if ( idProviderDescriptor != null )
        {
            final AuthControllerScript authControllerScript =
                authControllerScriptFactory.fromScript( idProviderDescriptor.getResourceKey() );
            final String functionName = params.getFunctionName();
            if ( authControllerScript.hasMethod( functionName ) )
            {
                PortalRequest portalRequest = params.getPortalRequest();
                if ( portalRequest == null )
                {
                    portalRequest = new PortalRequestAdapter().
                        adapt( params.getServletRequest() );
                }
                portalRequest.setApplicationKey( idProviderDescriptor.getKey() );
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

    private IdProviderDescriptor retrieveIdProviderDescriptor( final UserStore userStore )
    {
        if ( userStore != null )
        {
            final AuthConfig authConfig = userStore.getAuthConfig();
            if ( authConfig != null )
            {
                return idProviderDescriptorService.getDescriptor( authConfig.getApplicationKey() );
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
    public void setIdProviderDescriptorService( final IdProviderDescriptorService idProviderDescriptorService )
    {
        this.idProviderDescriptorService = idProviderDescriptorService;
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
