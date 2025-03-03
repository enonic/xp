package com.enonic.xp.portal.impl.idprovider;


import java.io.IOException;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.serializer.ResponseSerializationService;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Component
public class IdProviderControllerServiceImpl
    implements IdProviderControllerService
{
    private IdProviderControllerScriptFactory idProviderControllerScriptFactory;

    private IdProviderDescriptorService idProviderDescriptorService;

    private SecurityService securityService;

    private ResponseSerializationService responseSerializationService;

    @Override
    public PortalResponse execute( final IdProviderControllerExecutionParams params )
        throws IOException
    {
        final IdProviderKey idProviderKey = retrieveIdProviderKey( params );
        final IdProvider idProvider = retrieveIdProvider( idProviderKey );
        final IdProviderDescriptor idProviderDescriptor = retrieveIdProviderDescriptor( idProvider );

        if ( idProviderDescriptor != null )
        {
            final IdProviderControllerScript idProviderControllerScript =
                idProviderControllerScriptFactory.fromScript( idProviderDescriptor.getResourceKey() );
            final String functionName = params.getFunctionName();
            if ( idProviderControllerScript.hasMethod( functionName ) )
            {
                PortalRequest portalRequest = params.getPortalRequest();
                if ( portalRequest == null )
                {
                    portalRequest = new PortalRequestAdapter().adapt( params.getServletRequest() );
                }
                portalRequest.setApplicationKey( idProviderDescriptor.getKey() );
                portalRequest.setIdProvider( idProvider );

                final PortalResponse portalResponse = idProviderControllerScript.execute( functionName, portalRequest );

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

    private IdProviderKey retrieveIdProviderKey( IdProviderControllerExecutionParams params )
    {
        IdProviderKey idProviderKey = params.getIdProviderKey();
        if ( idProviderKey == null )
        {
            final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( params.getServletRequest() );
            if ( virtualHost != null )
            {
                idProviderKey = virtualHost.getDefaultIdProviderKey();
            }
        }
        return idProviderKey;
    }

    private IdProvider retrieveIdProvider( final IdProviderKey idProviderKey )
    {
        if ( idProviderKey != null )
        {
            return runWithAdminRole( () -> securityService.getIdProvider( idProviderKey ) );
        }
        return null;
    }

    private IdProviderDescriptor retrieveIdProviderDescriptor( final IdProvider idProvider )
    {
        if ( idProvider != null )
        {
            final IdProviderConfig idProviderConfig = idProvider.getIdProviderConfig();
            if ( idProviderConfig != null )
            {
                return idProviderDescriptorService.getDescriptor( idProviderConfig.getApplicationKey() );
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
    public void setIdProviderControllerScriptFactory( final IdProviderControllerScriptFactory idProviderControllerScriptFactory )
    {
        this.idProviderControllerScriptFactory = idProviderControllerScriptFactory;
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
