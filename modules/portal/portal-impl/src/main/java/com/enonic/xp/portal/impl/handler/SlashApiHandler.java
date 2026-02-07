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
import com.enonic.xp.portal.impl.websocket.WebSocketApiEndpointImpl;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.resource.ResourceKey;
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
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
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

    private final ExceptionMapper exceptionMapper;

    private final ExceptionRenderer exceptionRenderer;

    private final SiteService siteService;

    private final WebappService webappService;

    private final AdminToolDescriptorService adminToolDescriptorService;

    private final DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry;

    @Activate
    public SlashApiHandler( @Reference final ControllerScriptFactory controllerScriptFactory,
                            @Reference final ApiDescriptorService apiDescriptorService, @Reference final ExceptionMapper exceptionMapper,
                            @Reference final ExceptionRenderer exceptionRenderer, @Reference final SiteService siteService,
                            @Reference final WebappService webappService,
                            @Reference final AdminToolDescriptorService adminToolDescriptorService,
                            @Reference final DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry )
    {
        this.controllerScriptFactory = controllerScriptFactory;
        this.apiDescriptorService = apiDescriptorService;
        this.exceptionMapper = exceptionMapper;
        this.exceptionRenderer = exceptionRenderer;
        this.siteService = siteService;
        this.webappService = webappService;
        this.adminToolDescriptorService = adminToolDescriptorService;
        this.universalApiHandlerRegistry = universalApiHandlerRegistry;
    }

    public WebResponse handle( final WebRequest webRequest )
        throws Exception
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
            throw new IllegalStateException( "Cannot find api endpoint or api path in request" );
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

        verifyAccessToApi( apiDescriptor, portalRequest );
        verifyRequestMounted( apiDescriptor, portalRequest );

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
        throws Exception
    {
        final Trace trace = Tracer.newTrace( "universalAPI" );
        if ( trace == null )
        {
            return handleAPIRequest( portalRequest, supplier );
        }
        return Tracer.traceEx( trace, () -> {
            final WebResponse response = handleAPIRequest( portalRequest, supplier );
            addTranceInfo( trace, descriptorKey, response );
            return response;
        } );
    }

    private void verifyRequestMounted( final ApiDescriptor apiDescriptor, final PortalRequest portalRequest )
    {
        final String basePath = portalRequest.getBasePath();

        final DescriptorKey descriptorKey = apiDescriptor.getKey();

        final boolean result;
        if ( portalRequest.getEndpointPath() == null )
        {
            result = apiDescriptor.isMount();
        }
        else if ( PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            result = verifyRequestMountedOnSites( descriptorKey, portalRequest );
        }
        else if ( basePath.startsWith( PathMatchers.WEBAPP_PREFIX ) )
        {
            result = verifyPathMountedOnWebapps( descriptorKey, portalRequest );
        }
        else if ( basePath.startsWith( PathMatchers.ADMIN_TOOL_PREFIX ) )
        {
            result = verifyPathMountedOnAdminTool( descriptorKey, portalRequest );
        }
        else
        {
            result = false;
        }
        if ( !result )
        {
            throw WebException.notFound( String.format( "API [%s] is not mounted", descriptorKey ) );
        }
    }

    private boolean verifyPathMountedOnAdminTool( final DescriptorKey descriptorKey, final PortalRequest portalRequest )
    {
        final MatchResult matcher = PathMatchers.adminTool( portalRequest );
        if ( !matcher.hasMatch() )
        {
            return false;
        }

        final ApplicationKey applicationKey = HandlerHelper.resolveApplicationKey( matcher.group( "app" ) );
        final String tool = matcher.group( "tool" );

        final AdminToolDescriptor adminToolDescriptor = adminToolDescriptorService.getByKey( DescriptorKey.from( applicationKey, tool ) );
        if ( adminToolDescriptor == null )
        {
            return false;
        }

        return adminToolDescriptor.getApiMounts().contains( descriptorKey );
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
            final WebResponse returnedWebResponse = supplier.get();
            exceptionMapper.throwIfNeeded( returnedWebResponse );
            return returnedWebResponse;
        }
        catch ( Exception e )
        {
            return handleError( portalRequest, e );
        }
        finally
        {
            PortalRequestAccessor.remove();
        }
    }

    private void verifyAccessToApi( final ApiDescriptor apiDescriptor, final PortalRequest portalRequest )
    {
        if ( portalRequest.getBasePath().startsWith( PathMatchers.ADMIN_TOOL_PREFIX ) )
        {
            WebHandlerHelper.checkAdminLoginRole( portalRequest );
        }

        final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();
        if ( !apiDescriptor.isAccessAllowed( principals ) )
        {
            throw WebException.forbidden(
                String.format( "You don't have permission to access \"%s\" API for \"%s\"", apiDescriptor.getKey().getName(),
                               apiDescriptor.getKey().getApplicationKey() ) );
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

        return res;
    }

    private PortalResponse executeController( final PortalRequest req, final DescriptorKey descriptorKey )
    {
        final ControllerScript script = getScript( descriptorKey );
        final PortalResponse res = script.execute( req );
        final WebSocketConfig webSocketConfig = res.getWebSocket();

        applyWebSocketIfPresent( req.getWebSocketContext(), webSocketConfig,
                                 () -> new WebSocketEndpointImpl( webSocketConfig, () -> script ) );

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

    private ControllerScript getScript( final DescriptorKey descriptorKey )
    {
        final ResourceKey script = ApiDescriptor.toResourceKey( descriptorKey, "js" );
        return this.controllerScriptFactory.fromScript( script );
    }

    private WebResponse handleError( final WebRequest webRequest, final Exception e )
    {
        final WebException webException = exceptionMapper.map( e );
        final WebResponse webResponse = exceptionRenderer.render( webRequest, webException );
        webRequest.getRawRequest().setAttribute( "error.handled", Boolean.TRUE );
        return webResponse;
    }

}
