package com.enonic.xp.portal.impl.handler.service;

import java.io.IOException;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.ControllerWebHandlerWorker;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.service.ServiceDescriptorService;
import com.enonic.xp.site.Site;
import com.enonic.xp.util.Exceptions;
import com.enonic.xp.web.handler.WebResponse;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

final class ServiceWebHandlerWorker
    extends ControllerWebHandlerWorker
{
    private final static String ROOT_SERVICE_PREFIX = "services/";

    private final static String SITE_SERVICE_PREFIX = "site/services/";

    private final ResourceService resourceService;

    private final ServiceDescriptorService serviceDescriptorService;

    private final ControllerScriptFactory controllerScriptFactory;

    private final ApplicationKey applicationKey;

    private final String name;

    private ServiceWebHandlerWorker( final Builder builder )
    {
        super( builder );
        resourceService = builder.resourceService;
        serviceDescriptorService = builder.serviceDescriptorService;
        controllerScriptFactory = builder.controllerScriptFactory;
        applicationKey = builder.applicationKey;
        name = builder.name;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public PortalWebResponse execute()
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
        final Content content = getContentOrNull( getContentSelector() );
        final Site site = getSiteOrNull( content );
        final PortalWebRequest portalWebRequest = PortalWebRequest.create( this.webRequest ).
            applicationKey( applicationKey ).
            content( content ).
            site( site ).
            build();

        final PortalRequest portalRequest = convertToPortalRequest( portalWebRequest );

        //Executes the service
        final ControllerScript controllerScript = getScript();
        final PortalResponse portalResponse = PortalResponse.create( controllerScript.execute( portalRequest ) ).
            build();

        //Applies web socker endpoint if necessary
        final WebSocketContext webSocketContext = portalWebRequest.getWebSocketContext();
        final WebSocketConfig webSocketConfig = portalResponse.getWebSocket();
        if ( ( webSocketContext != null ) && ( webSocketConfig != null ) )
        {
            applyWebSocket( webSocketContext, webSocketConfig );
        }

        return convertToPortalWebResponse( portalResponse );
    }

    private void applyWebSocket( final WebSocketContext webSocketContext, final WebSocketConfig webSocketConfig )
    {
        final WebSocketEndpointImpl webSocketEndpoint = new WebSocketEndpointImpl( webSocketConfig, this::getScript );
        try
        {
            webSocketContext.apply( webSocketEndpoint );
        }
        catch ( IOException e )
        {
            Exceptions.unchecked( e );
        }
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

    @Override
    public WebSocketEndpoint newWebSocketEndpoint( final WebSocketConfig config )
    {
        return new WebSocketEndpointImpl( config, this::getScript );
    }

    public static final class Builder
        extends ControllerWebHandlerWorker.Builder<Builder, WebResponse>
    {
        private ResourceService resourceService;

        private ServiceDescriptorService serviceDescriptorService;

        private ControllerScriptFactory controllerScriptFactory;

        private ApplicationKey applicationKey;

        private String name;

        private Builder()
        {
        }

        public Builder resourceService( final ResourceService resourceService )
        {
            this.resourceService = resourceService;
            return this;
        }

        public Builder serviceDescriptorService( final ServiceDescriptorService serviceDescriptorService )
        {
            this.serviceDescriptorService = serviceDescriptorService;
            return this;
        }

        public Builder controllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
        {
            this.controllerScriptFactory = controllerScriptFactory;
            return this;
        }

        public Builder applicationKey( final ApplicationKey applicationKey )
        {
            this.applicationKey = applicationKey;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public ServiceWebHandlerWorker build()
        {
            return new ServiceWebHandlerWorker( this );
        }
    }
}
