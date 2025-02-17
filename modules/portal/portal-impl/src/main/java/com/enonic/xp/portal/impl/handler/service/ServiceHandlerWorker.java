package com.enonic.xp.portal.impl.handler.service;

import java.util.regex.Matcher;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.impl.app.WebAppHandler;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.service.ServiceDescriptorService;
import com.enonic.xp.site.Site;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

final class ServiceHandlerWorker
    extends PortalHandlerWorker<PortalRequest>
{
    private static final String ROOT_SERVICE_PREFIX = "services/";

    ServiceDescriptorService serviceDescriptorService;

    String name;

    ApplicationKey applicationKey;

    ControllerScriptFactory controllerScriptFactory;

    ContentResolver contentResolver;

    ServiceHandlerWorker( final PortalRequest request )
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
            throw WebException.notFound( String.format( "Service [%s] not found", descriptorKey ) );
        }

        //Checks if the access to ServiceDescriptor is allowed
        final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();
        if ( !serviceDescriptor.isAccessAllowed( principals ) )
        {
            throw WebException.forbidden( String.format( "You don't have permission to access [%s]", descriptorKey ) );
        }

        final ContentResolverResult resolvedContent = contentResolver.resolve( request );

        final Site site = resolvedContent.getNearestSite();

        //Checks if the application is set on the current site
        if ( site != null && site.getSiteConfigs().get( applicationKey ) == null )
        {
            throw WebException.forbidden( String.format( "Service [%s] forbidden for this site", descriptorKey ) );
        }

        //Checks if the application is set on the current application
        final ApplicationKey baseApplicationKey = getBaseApplicationKey();
        if ( baseApplicationKey != null && !baseApplicationKey.equals( applicationKey ) )
        {
            throw WebException.forbidden( String.format( "Service [%s] forbidden for this application", descriptorKey ) );
        }

        //Prepares the request
        this.request.setApplicationKey( applicationKey );
        this.request.setContent( resolvedContent.getContent() );
        this.request.setSite( site );

        //Executes the service
        final ControllerScript controllerScript = getScript();
        final PortalResponse portalResponse = controllerScript.execute( this.request );

        final WebSocketConfig webSocketConfig = portalResponse.getWebSocket();
        final WebSocketContext webSocketContext = this.request.getWebSocketContext();
        if ( ( webSocketContext != null ) && ( webSocketConfig != null ) )
        {
            final WebSocketEndpoint webSocketEndpoint = newWebSocketEndpoint( webSocketConfig, applicationKey );
            webSocketContext.apply( webSocketEndpoint );
        }

        return portalResponse;
    }

    private ControllerScript getScript()
    {
        return this.controllerScriptFactory.fromScript(
            ResourceKey.from( this.applicationKey, ROOT_SERVICE_PREFIX + this.name + "/" + this.name + ".js" ) );
    }

    private ApplicationKey getBaseApplicationKey()
    {
        final Matcher matcher = WebAppHandler.PATTERN.matcher( this.request.getRawPath() );
        if ( matcher.matches() )
        {
            final String applicationBase = matcher.group( 1 );
            return ApplicationKey.from( applicationBase );
        }
        return null;
    }

    private WebSocketEndpoint newWebSocketEndpoint( final WebSocketConfig config, final ApplicationKey app )
    {
        final Trace trace = Tracer.current();
        if ( trace != null && app != null && !trace.containsKey( "app" ) )
        {
            trace.put( "app", app.toString() );
        }
        return new WebSocketEndpointImpl( config, this::getScript );
    }

}
