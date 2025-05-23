package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.api.ApiMountDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.impl.api.DynamicUniversalApiHandler;
import com.enonic.xp.portal.impl.api.DynamicUniversalApiHandlerRegistry;
import com.enonic.xp.portal.impl.websocket.WebSocketApiEndpointImpl;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.site.Site;
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
    private static final Predicate<WebRequest> IS_STANDARD_METHOD = req -> HttpMethod.standard().contains( req.getMethod() );

    private static final Pattern API_PATTERN = Pattern.compile( "^/(_|api)/(?<appKey>[^/]+):(?<apiKey>[^/]+)/?" );

    private static final Pattern MOUNT_WEBAPP_ENDPOINT_PATTERN = Pattern.compile( "^/webapp/(?<baseAppKey>[^/]+)(?<restPath>.*?)/_/" );

    private static final Pattern MOUNT_ADMINTOOL_API_PATTERN = Pattern.compile( "^/admin/(?<appKey>[^/]+)/(?<tool>[^/]+)/_/" );

    private final ControllerScriptFactory controllerScriptFactory;

    private final ApiDescriptorService apiDescriptorService;

    private final ContentService contentService;

    private final ProjectService projectService;

    private final ExceptionMapper exceptionMapper;

    private final ExceptionRenderer exceptionRenderer;

    private final SiteService siteService;

    private final WebappService webappService;

    private final AdminToolDescriptorService adminToolDescriptorService;

    private final DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry;

    @Activate
    public SlashApiHandler( @Reference final ControllerScriptFactory controllerScriptFactory,
                            @Reference final ApiDescriptorService apiDescriptorService, @Reference final ContentService contentService,
                            @Reference final ProjectService projectService, @Reference final ExceptionMapper exceptionMapper,
                            @Reference final ExceptionRenderer exceptionRenderer, @Reference final SiteService siteService,
                            @Reference final WebappService webappService,
                            @Reference final AdminToolDescriptorService adminToolDescriptorService,
                            @Reference final DynamicUniversalApiHandlerRegistry universalApiHandlerRegistry )
    {
        this.controllerScriptFactory = controllerScriptFactory;
        this.apiDescriptorService = apiDescriptorService;
        this.contentService = contentService;
        this.projectService = projectService;
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
        final String path = Objects.requireNonNullElse( webRequest.getEndpointPath(), webRequest.getRawPath() );
        final Matcher matcher = API_PATTERN.matcher( path );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Invalid API path: " + path );
        }

        final ApplicationKey applicationKey = HandlerHelper.resolveApplicationKey( matcher.group( "appKey" ) );

        if ( !IS_STANDARD_METHOD.test( webRequest ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", webRequest.getMethod() ) );
        }

        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( HttpMethod.standard() );
        }

        final PortalRequest portalRequest = createPortalRequest( webRequest, applicationKey );

        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, Objects.requireNonNull( matcher.group( "apiKey" ) ) );

        final DynamicUniversalApiHandler dynamicApiHandler = universalApiHandlerRegistry.getApiHandler( descriptorKey );
        final ApiDescriptor apiDescriptor = resolveApiDescriptor( dynamicApiHandler, descriptorKey );

        if ( apiDescriptor == null )
        {
            throw WebException.notFound( String.format( "API [%s] not found", descriptorKey ) );
        }

        verifyAccessToApi( apiDescriptor );
        if ( !verifyRequestMounted( apiDescriptor, portalRequest ) )
        {
            throw WebException.notFound( String.format( "API [%s] is not mounted", descriptorKey ) );
        }

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
        final Trace trace = Tracer.newTrace( "UniversalAPI" );
        if ( trace == null )
        {
            return handleAPIRequest( portalRequest, supplier );
        }
        return Tracer.traceEx( trace, () -> {
            final WebResponse response = handleAPIRequest( portalRequest, supplier );
            addTranceInfo( trace, descriptorKey, portalRequest.getRawPath(), response );
            return response;
        } );
    }

    private boolean verifyRequestMounted( final ApiDescriptor apiDescriptor, final PortalRequest portalRequest )
    {
        final String rawPath = portalRequest.getRawPath();
        final DescriptorKey descriptorKey = apiDescriptor.getKey();

        if ( portalRequest.getEndpointPath() == null )
        {
            return rawPath.startsWith( "/api/" ) && apiDescriptor.isMount();
        }
        else if ( rawPath.startsWith( "/site/" ) || rawPath.startsWith( "/admin/site/" ) )
        {
            return verifyRequestMountedOnSites( descriptorKey, portalRequest );
        }
        else if ( rawPath.startsWith( "/webapp/" ) )
        {
            return verifyPathMountedOnWebapps( descriptorKey, portalRequest );
        }
        else if ( rawPath.startsWith( "/admin/" ) )
        {
            return verifyPathMountedOnAdminTool( descriptorKey, portalRequest );
        }
        else
        {
            return false;
        }
    }

    private boolean verifyPathMountedOnAdminTool( final DescriptorKey descriptorKey, final PortalRequest portalRequest )
    {
        final Matcher matcher = MOUNT_ADMINTOOL_API_PATTERN.matcher( portalRequest.getRawPath() );
        if ( !matcher.find() )
        {
            return false;
        }

        final ApplicationKey applicationKey = HandlerHelper.resolveApplicationKey( matcher.group( "appKey" ) );
        final String tool = matcher.group( "tool" );

        final AdminToolDescriptor adminToolDescriptor = adminToolDescriptorService.getByKey( DescriptorKey.from( applicationKey, tool ) );
        if ( adminToolDescriptor == null )
        {
            return false;
        }

        return adminToolDescriptor.getApiMounts()
            .stream()
            .anyMatch( descriptor -> descriptor.getApiKey().equals( descriptorKey.getName() ) &&
                descriptor.getApplicationKey().equals( descriptorKey.getApplicationKey() ) );
    }

    private boolean verifyPathMountedOnWebapps( final DescriptorKey descriptorKey, final PortalRequest portalRequest )
    {
        final Matcher webappMatcher = MOUNT_WEBAPP_ENDPOINT_PATTERN.matcher( portalRequest.getRawPath() );
        if ( !webappMatcher.find() )
        {
            return false;
        }

        final ApplicationKey baseAppKey = HandlerHelper.resolveApplicationKey( webappMatcher.group( "baseAppKey" ) );

        final WebappDescriptor webappDescriptor = webappService.getDescriptor( baseAppKey );
        if ( webappDescriptor == null )
        {
            return false;
        }

        return webappDescriptor.getApiMounts()
            .stream()
            .anyMatch( descriptor -> descriptor.getApiKey().equals( descriptorKey.getName() ) &&
                descriptor.getApplicationKey().equals( descriptorKey.getApplicationKey() ) );
    }

    private boolean verifyRequestMountedOnSites( final DescriptorKey descriptorKey, final PortalRequest portalRequest )
    {
        if ( "media".equals( descriptorKey.getApplicationKey().toString() ) )
        {
            return true;
        }

        final ContentResolverResult contentResolverResult = new ContentResolver( contentService ).resolve( portalRequest );

        final Site site = contentResolverResult.getNearestSite();

        portalRequest.setSite( site );
        portalRequest.setContent( contentResolverResult.getContent() );

        final SiteConfigs siteConfigs = resolveSiteConfigs( site, portalRequest.getRepositoryId() );

        if ( siteConfigs.isEmpty() )
        {
            return false;
        }

        for ( SiteConfig siteConfig : siteConfigs )
        {
            final SiteDescriptor siteDescriptor = siteService.getDescriptor( siteConfig.getApplicationKey() );

            if ( siteDescriptor != null )
            {
                final ApiMountDescriptor apiMountDescriptor = siteDescriptor.getApiDescriptors()
                    .stream()
                    .filter( descriptor -> descriptor.getApiKey().equals( descriptorKey.getName() ) &&
                        descriptor.getApplicationKey().equals( descriptorKey.getApplicationKey() ) )
                    .findAny()
                    .orElse( null );

                if ( apiMountDescriptor != null )
                {
                    return "/".equals( contentResolverResult.getSiteRelativePath() );
                }
            }
        }

        return false;
    }

    private SiteConfigs resolveSiteConfigs( final Site site, final RepositoryId repositoryId )
    {
        if ( site != null )
        {
            return site.getSiteConfigs();
        }
        else
        {
            final Project project = projectService.get( ProjectName.from( repositoryId ) );
            return project == null ? SiteConfigs.empty() : project.getSiteConfigs();
        }
    }

    private static void addTranceInfo( final Trace trace, final DescriptorKey descriptorKey, final String rawPath,
                                       final WebResponse response )
    {
        trace.put( "app", descriptorKey.getApplicationKey().toString() );
        trace.put( "api", descriptorKey.getName() );
        trace.put( "rawPath", rawPath );
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

    private void verifyAccessToApi( final ApiDescriptor apiDescriptor )
    {
        final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();
        if ( !apiDescriptor.isAccessAllowed( principals ) )
        {
            throw WebException.forbidden(
                String.format( "You don't have permission to access \"%s\" API for \"%s\"", apiDescriptor.getKey().getName(),
                               apiDescriptor.getKey().getApplicationKey() ) );
        }
    }

    private PortalRequest createPortalRequest( final WebRequest webRequest, final ApplicationKey applicationKey )
    {
        final PortalRequest portalRequest =
            webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );

        portalRequest.setContextPath( portalRequest.getBaseUri() );
        portalRequest.setApplicationKey( applicationKey );

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
