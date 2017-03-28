package com.enonic.xp.portal.impl.handler.service;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.service.ServiceDescriptorService;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

final class ServiceHandlerWorker
    extends ControllerHandlerWorker
{
    private final static String ROOT_SERVICE_PREFIX = "services/";

    private final static String SITE_SERVICE_PREFIX = "site/services/";

    protected ResourceService resourceService;

    protected ServiceDescriptorService serviceDescriptorService;

    protected String name;

    protected ApplicationKey applicationKey;

    protected ControllerScriptFactory controllerScriptFactory;

    public ServiceHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        //Retrieves the ServiceDescriptor
        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, name );
        final ServiceDescriptor serviceDescriptor = serviceDescriptorService.getByKey( descriptorKey );
        if ( serviceDescriptor == null )
        {
            throw notFound( "Service [%s] not found", descriptorKey.toString() );
        }

        //Checks if the access to ServiceDescriptor is allowed
        final PrincipalKeys principals = ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
        if ( !serviceDescriptor.isAccessAllowed( principals ) )
        {
            throw forbidden( "You don't have permission to access [%s]", serviceDescriptor.toString() );
        }

        //Prepares the request
        this.request.setApplicationKey( applicationKey );
        final Content content = getContentOrNull( getContentSelector() );
        this.request.setContent( content );
        final Site site = getSiteOrNull( content );
        this.request.setSite( site );

        //Executes the service
        final ControllerScript controllerScript = getScript();
        final PortalResponse portalResponse = controllerScript.execute( this.request );

        final WebSocketConfig webSocketConfig = portalResponse.getWebSocket();
        final WebSocketContext webSocketContext = this.request.getWebSocketContext();
        if ( ( webSocketContext != null ) && ( webSocketConfig != null ) )
        {
            final WebSocketEndpoint webSocketEndpoint = newWebSocketEndpoint( webSocketConfig );
            webSocketContext.apply( webSocketEndpoint );
        }

        return portalResponse;
    }

    private ControllerScript getScript()
    {
        //Retrieves the resource
        Resource resource = this.resourceService.getResource( ResourceKey.from( this.applicationKey, ROOT_SERVICE_PREFIX + this.name ) );
        if ( !resource.exists() )
        {
            resource = this.resourceService.getResource( ResourceKey.from( this.applicationKey, SITE_SERVICE_PREFIX + this.name ) );
        }

        //Executes the service
        return this.controllerScriptFactory.fromDir( resource.getKey() );
    }

    private WebSocketEndpoint newWebSocketEndpoint( final WebSocketConfig config )
    {
        return new WebSocketEndpointImpl( config, this::getScript );
    }
}
