package com.enonic.xp.portal.impl.handler.service;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.portal.impl.app.AppHandler;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.service.ServiceDescriptorService;
import com.enonic.xp.site.Site;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
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
            throw forbidden( "You don't have permission to access [%s]", descriptorKey.toString() );
        }

        //Retrieves the current content and site
        final Content content = getContentOrNull( getContentSelector() );
        final Site site = getSiteOrNull( content );

        //Checks if the application is set on the current site
        final Site forcedSite = site == null ? forcedGetSiteOrNull(content) : site;
        if ( forcedSite != null )
        {
            final PropertyTree siteConfig = forcedSite.getSiteConfig( applicationKey );
            if ( siteConfig == null )
            {
                throw forbidden( "Service [%s] forbidden for this site", descriptorKey.toString() );
            }
        }

        //Checks if the application is set on the current application
        final ApplicationKey baseApplicationKey = getBaseApplicationKey();
        if ( baseApplicationKey != null && !baseApplicationKey.equals( applicationKey ) )
        {
            throw forbidden( "Service [%s] forbidden for this application", descriptorKey.toString() );
        }

        //Prepares the request
        this.request.setApplicationKey( applicationKey );
        this.request.setContent( content );
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

    private ApplicationKey getBaseApplicationKey()
    {
        final Matcher matcher = AppHandler.PATTERN.matcher( this.request.getRawPath() );
        if ( matcher.matches() )
        {
            final String applicationBase = matcher.group( 1 );
            return ApplicationKey.from( applicationBase );
        }
        return null;
    }

    private WebSocketEndpoint newWebSocketEndpoint( final WebSocketConfig config )
    {
        final ApplicationKey app = this.request.getApplicationKey();
        final Trace trace = Tracer.current();
        if ( trace != null && app != null && !trace.containsKey( "app" ) )
        {
            trace.put( "app", app.toString() );
        }
        return new WebSocketEndpointImpl( config, this::getScript );
    }
    
    private Site forcedGetSiteOrNull( final Content content ) {
        return runWithAdminRole(() -> {
            final Content forcedContent = content == null ? getContentOrNull( getContentSelector() ) : content;
            return getSiteOrNull( forcedContent );
        });
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
