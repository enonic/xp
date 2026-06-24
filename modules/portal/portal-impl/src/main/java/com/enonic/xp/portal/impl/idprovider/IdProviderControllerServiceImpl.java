package com.enonic.xp.portal.impl.idprovider;


import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.serializer.WebSerializerService;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static java.util.Objects.requireNonNull;

@Component
public class IdProviderControllerServiceImpl
    implements IdProviderControllerService
{
    private IdProviderControllerScriptFactory idProviderControllerScriptFactory;

    private IdProviderDescriptorService idProviderDescriptorService;

    private SecurityService securityService;

    private WebSerializerService webSerializerService;

    @Override
    public PortalResponse executeResponse( final IdProviderControllerExecutionParams params )
        throws IOException
    {
        final Prepared prepared = prepare( params );
        if ( prepared == null )
        {
            return null;
        }

        final PortalResponse portalResponse = prepared.script.execute( prepared.functionName, prepared.portalRequest );

        if ( portalResponse != null && params.getResponse() != null )
        {
            webSerializerService.response( prepared.portalRequest, portalResponse, params.getResponse() );
        }
        return portalResponse;
    }

    @Override
    public Object executeFunction( final IdProviderControllerExecutionParams params )
        throws IOException
    {
        final Prepared prepared = prepare( params );
        return prepared == null ? null : prepared.script.executeFunction( prepared.functionName, prepared.portalRequest );
    }

    /**
     * Resolves the controller script, the function to call and the portal request to call it with -
     * the shared prelude for {@link #executeResponse} and {@link #executeFunction}. Returns
     * {@code null} when the id provider has no controller or does not implement the function.
     */
    private Prepared prepare( final IdProviderControllerExecutionParams params )
    {
        final IdProviderKey idProviderKey = retrieveIdProviderKey( params );
        final IdProvider idProvider = retrieveIdProvider( idProviderKey );
        final IdProviderDescriptor idProviderDescriptor = retrieveIdProviderDescriptor( idProvider );

        if ( idProviderDescriptor == null )
        {
            return null;
        }

        final IdProviderControllerScript idProviderControllerScript =
            idProviderControllerScriptFactory.fromScript( getScriptResourceKey( idProviderDescriptor.getKey() ) );
        final String functionName =
            resolveFunctionName( params.getFunctionName(), idProviderControllerScript, params.getPortalRequest() );
        if ( functionName == null )
        {
            return null;
        }

        final PortalRequest portalRequest;
        if ( params.getServletRequest() != null )
        {
            portalRequest = new PortalRequestAdapter().adapt( webSerializerService.request( params.getServletRequest() ) );
        }
        else
        {
            portalRequest = requireNonNull( params.getPortalRequest() );
        }

        portalRequest.setApplicationKey( idProviderDescriptor.getKey() );
        portalRequest.setIdProvider( idProvider );

        return new Prepared( idProviderControllerScript, functionName, portalRequest );
    }

    private static final class Prepared
    {
        final IdProviderControllerScript script;

        final String functionName;

        final PortalRequest portalRequest;

        Prepared( final IdProviderControllerScript script, final String functionName, final PortalRequest portalRequest )
        {
            this.script = script;
            this.functionName = functionName;
            this.portalRequest = portalRequest;
        }
    }

    @Override
    public boolean hasFunction( final IdProviderKey idProviderKey, final String functionName )
    {
        final IdProvider idProvider = retrieveIdProvider( idProviderKey );
        final IdProviderDescriptor idProviderDescriptor = retrieveIdProviderDescriptor( idProvider );

        if ( idProviderDescriptor == null )
        {
            return false;
        }

        return idProviderControllerScriptFactory.fromScript( getScriptResourceKey( idProviderDescriptor.getKey() ) ).hasMethod( functionName );
    }

    private static String resolveFunctionName( final String exact, final IdProviderControllerScript idProviderControllerScript,
                                               final PortalRequest portalRequest )
    {
        if ( exact != null )
        {
            return idProviderControllerScript.hasMethod( exact ) ? exact : null;
        }
        final String method = portalRequest.getMethod().toString();
        if ( idProviderControllerScript.hasMethod( method ) )
        {
            return method;
        }
        final String lowerCaseMethod = method.toLowerCase( Locale.ROOT );
        if ( idProviderControllerScript.hasMethod( lowerCaseMethod ) )
        {
            return lowerCaseMethod;
        }
        return null;
    }

    private IdProviderKey retrieveIdProviderKey( IdProviderControllerExecutionParams params )
    {
        IdProviderKey idProviderKey = params.getIdProviderKey();
        if ( idProviderKey == null )
        {
            final HttpServletRequest servletRequest;
            final PortalRequest portalRequest = params.getPortalRequest();
            servletRequest = portalRequest == null ? params.getServletRequest() : portalRequest.getRawRequest();
            final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( servletRequest );
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
        final AuthenticationInfo authenticationInfo =
            AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build();
        return ContextBuilder.from( context ).authInfo( authenticationInfo ).build().callWith( callable );
    }

    public ResourceKey getScriptResourceKey( final ApplicationKey key )
    {
        return ResourceKey.from( key, "idprovider/idprovider.js" );
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
    public void setResponseSerializationService( final WebSerializerService webSerializerService )
    {
        this.webSerializerService = webSerializerService;
    }
}
