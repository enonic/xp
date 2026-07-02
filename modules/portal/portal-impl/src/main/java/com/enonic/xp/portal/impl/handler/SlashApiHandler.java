package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.MatchResult;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.impl.api.DynamicUniversalApiHandler;
import com.enonic.xp.portal.impl.api.DynamicUniversalApiHandlerRegistry;
import com.enonic.xp.portal.impl.sse.SseApiEndpointImpl;
import com.enonic.xp.portal.impl.sse.SseEndpointImpl;
import com.enonic.xp.portal.impl.websocket.WebSocketApiEndpointImpl;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.sse.SseConfig;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.webapp.WebappService;

@Component(service = SlashApiHandler.class)
public class SlashApiHandler
{
    private final ControllerScriptFactory controllerScriptFactory;

    private final ApiDescriptorService apiDescriptorService;

    private final SiteService siteService;

    private final WebappService webappService;

    private final AdminToolDescriptorService adminToolDescriptorService;

    private final DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry;

    private final SseManager sseManager;

    @Activate
    public SlashApiHandler( @Reference final ControllerScriptFactory controllerScriptFactory,
                            @Reference final ApiDescriptorService apiDescriptorService, @Reference final SiteService siteService,
                            @Reference final WebappService webappService,
                            @Reference final AdminToolDescriptorService adminToolDescriptorService,
                            @Reference final DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry,
                            @Reference final SseManager sseManager )
    {
        this.controllerScriptFactory = controllerScriptFactory;
        this.apiDescriptorService = apiDescriptorService;
        this.siteService = siteService;
        this.webappService = webappService;
        this.adminToolDescriptorService = adminToolDescriptorService;
        this.universalApiHandlerRegistry = universalApiHandlerRegistry;
        this.sseManager = sseManager;
    }

    public WebResponse handle( final WebRequest webRequest )
    {
        final String endpoint = HandlerHelper.findEndpoint( webRequest );
        final DescriptorKey descriptorKey;
        if ( webRequest.getBasePath().startsWith( PathMatchers.API_PREFIX ) )
        {
            final MatchResult matcher = PathMatchers.api( webRequest );
            if ( !matcher.hasMatch() )
            {
                throw WebException.notFound( "Invalid api path" );
            }
            descriptorKey = HandlerHelper.resolveDescriptorKey( matcher.group( "descriptor" ) );
        }
        else if ( endpoint != null )
        {
            descriptorKey = HandlerHelper.resolveDescriptorKey( endpoint );
        }
        else
        {
            final String rawPath = webRequest.getRawPath();
            final int secondSlash = rawPath.indexOf( '/', 1 );
            final String descriptor = secondSlash == -1 ? rawPath.substring( 1 ) : rawPath.substring( 1, secondSlash );
            descriptorKey = HandlerHelper.resolveDescriptorKey( descriptor );
        }

        if ( !HttpMethod.isStandard( webRequest.getMethod() ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", webRequest.getMethod() ) );
        }
        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( HttpMethod.standard() );
        }

        final PortalRequest portalRequest = createPortalRequest( webRequest, descriptorKey );

        final DynamicUniversalApiHandler dynamicApiHandler = universalApiHandlerRegistry.getApiHandler( descriptorKey );
        final ApiDescriptor apiDescriptor = resolveApiDescriptor( dynamicApiHandler, descriptorKey );

        if ( apiDescriptor == null )
        {
            throw WebException.notFound( String.format( "API [%s] not found", descriptorKey ) );
        }

        final MountContext mountContext = resolveMountContext( portalRequest );
        verifyAccessToApi( apiDescriptor, portalRequest, mountContext );
        verifyRequestMounted( apiDescriptor, portalRequest, mountContext );

        final Supplier<WebResponse> handler = dynamicApiHandler != null
            ? () -> executeDynamicApiHandler( portalRequest, dynamicApiHandler )
            : () -> executeController( portalRequest, descriptorKey );

        return execute( portalRequest, descriptorKey, handler );
    }

    private ApiDescriptor resolveApiDescriptor( DynamicUniversalApiHandler dynamicApiHandler, final DescriptorKey descriptorKey )
    {
        return dynamicApiHandler != null ? dynamicApiHandler.getApiDescriptor() : apiDescriptorService.getByKey( descriptorKey );
    }

    private WebResponse execute( final PortalRequest portalRequest, final DescriptorKey descriptorKey,
                                 final Supplier<WebResponse> supplier )
    {
        final Trace trace = Tracer.newTrace( "universalAPI" );
        if ( trace == null )
        {
            return handleAPIRequest( portalRequest, supplier );
        }
        return Tracer.trace( trace, () -> {
            final WebResponse response = handleAPIRequest( portalRequest, supplier );
            addTranceInfo( trace, descriptorKey, response );
            return response;
        } );
    }

    private MountContext resolveMountContext( final PortalRequest portalRequest )
    {
        if ( portalRequest.getEndpointPath() == null )
        {
            final String connector = portalRequest.getRawRequest() != null
                ? (String) portalRequest.getRawRequest().getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE )
                : null;

            if ( DispatchConstants.API_CONNECTOR.equals( connector ) )
            {
                return MountContext.connector( "management" );
            }
            else if ( DispatchConstants.XP_CONNECTOR.equals( connector ) )
            {
                return MountContext.connector( "web" );
            }
            return MountContext.none();
        }
        else if ( PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            return MountContext.site();
        }
        else if ( portalRequest.getBasePath().startsWith( PathMatchers.WEBAPP_PREFIX ) )
        {
            return MountContext.webapp();
        }
        else if ( portalRequest.getBasePath().startsWith( PathMatchers.ADMIN_TOOL_PREFIX ) )
        {
            return MountContext.adminTool( resolveAdminTool( portalRequest ) );
        }
        return MountContext.none();
    }

    private void verifyRequestMounted( final ApiDescriptor apiDescriptor, final PortalRequest portalRequest, final MountContext mountContext )
    {
        final DescriptorKey descriptorKey = apiDescriptor.getKey();

        final boolean result = switch ( mountContext.type() )
        {
            case CONNECTOR -> apiDescriptor.getMount().contains( mountContext.requiredMount() );
            case SITE -> verifyRequestMountedOnSites( descriptorKey, portalRequest );
            case WEBAPP -> verifyPathMountedOnWebapps( descriptorKey, portalRequest );
            case ADMIN_TOOL ->
                mountContext.adminTool() != null && mountContext.adminTool().getApiMounts().contains( descriptorKey );
            case NONE -> false;
        };

        if ( !result )
        {
            throw WebException.notFound( String.format( "API [%s] is not mounted", descriptorKey ) );
        }
    }

    private AdminToolDescriptor resolveAdminTool( final PortalRequest portalRequest )
    {
        final MatchResult matcher = PathMatchers.adminTool( portalRequest );
        if ( !matcher.hasMatch() )
        {
            return null;
        }

        final ApplicationKey applicationKey = HandlerHelper.resolveApplicationKey( matcher.group( "app" ) );
        final String tool = matcher.group( "tool" );

        return adminToolDescriptorService.getByKey( DescriptorKey.from( applicationKey, tool ) );
    }

    private boolean verifyPathMountedOnWebapps( final DescriptorKey descriptorKey, final PortalRequest portalRequest )
    {
        final MatchResult matcher = PathMatchers.webapp( portalRequest );
        if ( !matcher.hasMatch() )
        {
            return false;
        }

        final ApplicationKey baseAppKey = HandlerHelper.resolveApplicationKey( matcher.group( "app" ) );

        final WebappDescriptor webappDescriptor = webappService.getDescriptor( baseAppKey );
        if ( webappDescriptor == null )
        {
            return false;
        }

        return webappDescriptor.getApiMounts().contains( descriptorKey );
    }

    private boolean verifyRequestMountedOnSites( final DescriptorKey descriptorKey, final PortalRequest portalRequest )
    {
        if ( !"/".equals( PortalRequestHelper.getSiteRelativePath( portalRequest ) ) )
        {
            return false;
        }

        if ( ApplicationKey.MEDIA_MOD.equals( descriptorKey.getApplicationKey() ) )
        {
            return true;
        }

        final SiteConfigs siteConfigs = PortalRequestHelper.getSiteConfigs( portalRequest );

        return siteConfigs.stream()
            .map( SiteConfig::getApplicationKey )
            .map( siteService::getDescriptor )
            .filter( Objects::nonNull )
            .map( SiteDescriptor::getApiMounts )
            .anyMatch( mounts -> mounts.contains( descriptorKey ) );
    }

    private static void addTranceInfo( final Trace trace, final DescriptorKey descriptorKey, final WebResponse response )
    {
        trace.put( "app", descriptorKey.getApplicationKey().toString() );
        trace.put( "api", descriptorKey.getName() );
        HandlerHelper.addTraceInfo( trace, response );
    }

    private WebResponse handleAPIRequest( final PortalRequest portalRequest, final Supplier<WebResponse> supplier )
    {
        PortalRequestAccessor.set( portalRequest );
        try
        {
            return supplier.get();
        }
        finally
        {
            PortalRequestAccessor.remove();
        }
    }

    private void verifyAccessToApi( final ApiDescriptor apiDescriptor, final PortalRequest portalRequest, final MountContext mountContext )
    {
        final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();

        if ( portalRequest.getBasePath().startsWith( PathMatchers.ADMIN_TOOL_PREFIX ) )
        {
            WebHandlerHelper.checkAdminLoginRole( portalRequest );
        }

        if ( mountContext.type() == MountContext.Type.ADMIN_TOOL && mountContext.adminTool() != null &&
            !mountContext.adminTool().isAccessAllowed( principals ) )
        {
            throw forbidden( apiDescriptor );
        }

        if ( !apiDescriptor.isAccessAllowed( principals ) )
        {
            throw forbidden( apiDescriptor );
        }
    }

    private static WebException forbidden( final ApiDescriptor apiDescriptor )
    {
        return WebException.forbidden(
            String.format( "You don't have permission to access \"%s\" API for \"%s\"", apiDescriptor.getKey().getName(),
                           apiDescriptor.getKey().getApplicationKey() ) );
    }

    private record MountContext(Type type, String requiredMount, AdminToolDescriptor adminTool)
    {
        private enum Type
        {
            CONNECTOR, SITE, WEBAPP, ADMIN_TOOL, NONE
        }

        private static MountContext connector( final String requiredMount )
        {
            return new MountContext( Type.CONNECTOR, requiredMount, null );
        }

        private static MountContext site()
        {
            return new MountContext( Type.SITE, null, null );
        }

        private static MountContext webapp()
        {
            return new MountContext( Type.WEBAPP, null, null );
        }

        private static MountContext adminTool( final AdminToolDescriptor adminTool )
        {
            return new MountContext( Type.ADMIN_TOOL, null, adminTool );
        }

        private static MountContext none()
        {
            return new MountContext( Type.NONE, null, null );
        }
    }

    private PortalRequest createPortalRequest( final WebRequest webRequest, final DescriptorKey descriptorKey )
    {
        final PortalRequest portalRequest =
            webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );

        if ( webRequest.getEndpointPath() != null )
        {
            portalRequest.setContextPath( webRequest.getBasePath() + "/_/" + descriptorKey );
        }
        else
        {
            final String baseUri = PathMatchers.API_PREFIX + descriptorKey;
            portalRequest.setBaseUri( baseUri );
            portalRequest.setContextPath( baseUri );
        }

        portalRequest.setApplicationKey( descriptorKey.getApplicationKey() );

        return portalRequest;
    }

    private WebResponse executeDynamicApiHandler( final PortalRequest req, final DynamicUniversalApiHandler dynamicApiHandler )
    {
        final WebResponse res = dynamicApiHandler.handle( req );
        final WebSocketConfig webSocketConfig = res.getWebSocket();

        applyWebSocketIfPresent( req.getWebSocketContext(), webSocketConfig,
                                 () -> new WebSocketApiEndpointImpl( webSocketConfig, () -> dynamicApiHandler ) );

        final SseConfig sseConfig = res.getSse();
        if ( sseConfig != null )
        {
            final SseApiEndpointImpl sseEndpoint = new SseApiEndpointImpl( sseConfig, () -> dynamicApiHandler );
            this.sseManager.setupSse( req, sseEndpoint );
        }

        return res;
    }

    private PortalResponse executeController( final PortalRequest req, final DescriptorKey descriptorKey )
    {
        final ControllerScript script = getScript( descriptorKey );
        final PortalResponse res = script.execute( req );
        final WebSocketConfig webSocketConfig = res.getWebSocket();

        applyWebSocketIfPresent( req.getWebSocketContext(), webSocketConfig,
                                 () -> new WebSocketEndpointImpl( webSocketConfig, () -> script ) );

        final SseConfig sseConfig = res.getSse();
        if ( sseConfig != null )
        {
            final SseEndpointImpl sseEndpoint = new SseEndpointImpl( sseConfig, () -> script );
            this.sseManager.setupSse( req, sseEndpoint );
        }

        return res;
    }

    private void applyWebSocketIfPresent( final WebSocketContext webSocketContext, final WebSocketConfig webSocketConfig,
                                          final Supplier<WebSocketEndpoint> webSocketEndpointSupplier )
    {
        if ( webSocketContext != null && webSocketConfig != null )
        {
            try
            {
                webSocketContext.apply( webSocketEndpointSupplier.get() );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }
    }

    private ControllerScript getScript( final DescriptorKey key )
    {
        return this.controllerScriptFactory.fromScript( apiDescriptorService.getControllerResourceKey( key ) );
    }

}
