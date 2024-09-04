package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.api.ApiMountDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKey;
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
import com.enonic.xp.web.universalapi.UniversalApiHandler;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.webapp.WebappService;

@Component(service = SlashApiHandler.class)
public class SlashApiHandler
{
    private static final Predicate<WebRequest> IS_STANDARD_METHOD = req -> HttpMethod.standard().contains( req.getMethod() );

    private static final Pattern API_PATTERN = Pattern.compile( "^/(_|api)/(?<appKey>[^/]+)(?:/(?<apiKey>[^/]+))?(?<restPath>/.*)?$" );

    private static final Pattern MOUNT_WEBAPP_ENDPOINT_PATTERN = Pattern.compile( "^/webapp/(?<baseAppKey>[^/]+)(?<restPath>.*?)/_/" );

    private static final Pattern MOUNT_ADMINTOOL_API_PATTERN = Pattern.compile( "^/admin/(?<appKey>[^/]+)/(?<tool>[^/]+)/_/" );

    private static final List<String> RESERVED_APP_KEYS =
        List.of( "attachment", "image", "error", "idprovider", "service", "asset", "component", "widgets", "media" );

    private static final String UNNAMED_API_KEY = "";

    private final ControllerScriptFactory controllerScriptFactory;

    private final ApiDescriptorService apiDescriptorService;

    private final ContentService contentService;

    private final ProjectService projectService;

    private final ExceptionMapper exceptionMapper;

    private final ExceptionRenderer exceptionRenderer;

    private final SiteService siteService;

    private final WebappService webappService;

    private final AdminToolDescriptorService adminToolDescriptorService;

    private final ConcurrentMap<DescriptorKey, UniversalApiHandlerWrapper> dynamicApiHandlers = new ConcurrentHashMap<>();

    @Activate
    public SlashApiHandler( @Reference final ControllerScriptFactory controllerScriptFactory,
                            @Reference final ApiDescriptorService apiDescriptorService, @Reference final ContentService contentService,
                            @Reference final ProjectService projectService, @Reference final ExceptionMapper exceptionMapper,
                            @Reference final ExceptionRenderer exceptionRenderer, @Reference final SiteService siteService,
                            @Reference final WebappService webappService,
                            @Reference final AdminToolDescriptorService adminToolDescriptorService )
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
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addApiHandler( final UniversalApiHandler apiHandler, final Map<String, ?> properties )
    {
        final ApiDescriptor apiDescriptor = createDynamicApiDescriptor( properties );
        this.dynamicApiHandlers.put( apiDescriptor.getKey(), new UniversalApiHandlerWrapper( apiHandler, apiDescriptor ) );
    }

    public void removeApiHandler( final UniversalApiHandler apiHandler )
    {
        dynamicApiHandlers.values()
            .stream()
            .filter( wrapper -> wrapper.apiHandler.equals( apiHandler ) )
            .findFirst()
            .ifPresent( apiHandlerWrapper -> this.dynamicApiHandlers.remove( apiHandlerWrapper.apiDescriptor.getKey() ) );
    }

    public WebResponse handle( final WebRequest webRequest )
        throws Exception
    {
        final String path = Objects.requireNonNullElse( webRequest.getEndpointPath(), webRequest.getRawPath() );
        Matcher matcher = API_PATTERN.matcher( path );
        if ( !matcher.matches() )
        {
            throw new IllegalArgumentException( "Invalid API path: " + path );
        }

        final ApplicationKey applicationKey = HandlerHelper.resolveApplicationKey( matcher.group( "appKey" ) );

        if ( RESERVED_APP_KEYS.contains( applicationKey.getName() ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Application key [%s] is reserved", applicationKey ) );
        }

        if ( !IS_STANDARD_METHOD.test( webRequest ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", webRequest.getMethod() ) );
        }

        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( HttpMethod.standard() );
        }

        final String apiKey = Objects.requireNonNullElse( matcher.group( "apiKey" ), UNNAMED_API_KEY );

        final String restPath = matcher.group( "restPath" );

        final PortalRequest portalRequest = createPortalRequest( webRequest, applicationKey );

        final UniversalApiHandlerWrapper dynamicApiHandler = resolveDynamicApiHandler( applicationKey, apiKey, portalRequest );

        if ( dynamicApiHandler != null )
        {
            return execute( portalRequest, dynamicApiHandler.apiDescriptor.getKey(), restPath,
                            () -> dynamicApiHandler.apiHandler.handle( portalRequest ) );
        }
        else
        {
            final DescriptorKey descriptorKey = resolveDescriptorKey( applicationKey, apiKey, portalRequest );
            return execute( portalRequest, descriptorKey, restPath, () -> executeController( portalRequest, descriptorKey ) );
        }
    }

    private DescriptorKey resolveDescriptorKey( final ApplicationKey applicationKey, final String apiKey,
                                                final PortalRequest portalRequest )
    {
        // If the unnamed API is present, then the named APIs are skipped.
        DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, UNNAMED_API_KEY );
        ApiDescriptor apiDescriptor = apiDescriptorService.getByKey( descriptorKey );
        if ( apiDescriptor == null )
        {
            descriptorKey = DescriptorKey.from( applicationKey, apiKey );
            apiDescriptor = apiDescriptorService.getByKey( descriptorKey );
        }
        if ( apiDescriptor != null )
        {
            verifyAccessToApi( apiDescriptor );
        }
        verifyRequestMounted( descriptorKey, portalRequest );

        return descriptorKey;
    }

    private UniversalApiHandlerWrapper resolveDynamicApiHandler( final ApplicationKey applicationKey, final String apiKey,
                                                                 final PortalRequest portalRequest )
    {
        // If the unnamed API is present, then the named APIs are skipped.
        UniversalApiHandlerWrapper handlerWrapper = dynamicApiHandlers.get( DescriptorKey.from( applicationKey, UNNAMED_API_KEY ) );
        if ( handlerWrapper == null )
        {
            handlerWrapper = dynamicApiHandlers.get( DescriptorKey.from( applicationKey, apiKey ) );
        }

        if ( handlerWrapper == null )
        {
            return null;
        }

        verifyAccessToApi( handlerWrapper.apiDescriptor );
        verifyRequestMounted( handlerWrapper.apiDescriptor.getKey(), portalRequest );

        return handlerWrapper;
    }

    private WebResponse execute( final PortalRequest portalRequest, final DescriptorKey descriptorKey, final String restPath,
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
            addTranceInfo( trace, descriptorKey, restPath, response );
            return response;
        } );
    }

    private void verifyRequestMounted( final DescriptorKey descriptorKey, final PortalRequest portalRequest )
    {
        final String rawPath = portalRequest.getRawPath();

        boolean isMounted = false;

        if ( portalRequest.getEndpointPath() == null && rawPath.startsWith( "/api/" ) )
        {
            isMounted = true;
        }
        else if ( portalRequest.getEndpointPath() != null && rawPath.startsWith( "/site/" ) || rawPath.startsWith( "/admin/site/" ) )
        {
            isMounted = verifyRequestMountedOnSites( descriptorKey, portalRequest );
        }
        else if ( portalRequest.getEndpointPath() != null && rawPath.startsWith( "/webapp/" ) )
        {
            isMounted = verifyPathMountedOnWebapps( descriptorKey, portalRequest );
        }
        else if ( portalRequest.getEndpointPath() != null && rawPath.startsWith( "/admin/" ) )
        {
            isMounted = verifyPathMountedOnAdminTool( descriptorKey, portalRequest );
        }

        if ( !isMounted )
        {
            throw WebException.notFound( String.format( "API [%s] is not mounted", descriptorKey ) );
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
        final ContentResolverResult contentResolverResult = new ContentResolver( contentService ).resolve( portalRequest );

        final Site site = contentResolverResult.getNearestSite();

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

    private static void addTranceInfo( final Trace trace, final DescriptorKey descriptorKey, final String restPath,
                                       final WebResponse response )
    {
        trace.put( "app", descriptorKey.getApplicationKey().toString() );
        trace.put( "api", descriptorKey.getName() );
        trace.put( "path", restPath );
        HandlerHelper.addTraceInfo( trace, response );
    }

    private WebResponse handleAPIRequest( final PortalRequest portalRequest, final Supplier<WebResponse> supplier )
    {
        try
        {
            PortalRequestAccessor.set( portalRequest.getRawRequest(), portalRequest );

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

    private PortalResponse executeController( final PortalRequest req, final DescriptorKey descriptorKey )
    {
        final ControllerScript script = getScript( descriptorKey );
        final PortalResponse res = script.execute( req );

        final WebSocketConfig webSocketConfig = res.getWebSocket();
        final WebSocketContext webSocketContext = req.getWebSocketContext();
        if ( webSocketContext != null && webSocketConfig != null )
        {
            final WebSocketEndpoint webSocketEndpoint =
                newWebSocketEndpoint( webSocketConfig, script, descriptorKey.getApplicationKey() );
            try
            {
                webSocketContext.apply( webSocketEndpoint );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }

        return res;
    }

    private WebSocketEndpoint newWebSocketEndpoint( final WebSocketConfig config, final ControllerScript script,
                                                    final ApplicationKey applicationKey )
    {
        final Trace trace = Tracer.current();
        if ( trace != null && !trace.containsKey( "app" ) )
        {
            trace.put( "app", applicationKey.toString() );
        }
        return new WebSocketEndpointImpl( config, () -> script );
    }

    private ControllerScript getScript( final DescriptorKey descriptorKey )
    {
        final ResourceKey script = ApiDescriptor.toResourceKey( descriptorKey, "js" );
        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "script", script.getPath() );
        }
        return this.controllerScriptFactory.fromScript( script );
    }

    private WebResponse handleError( final WebRequest webRequest, final Exception e )
    {
        final WebException webException = exceptionMapper.map( e );
        final WebResponse webResponse = exceptionRenderer.render( webRequest, webException );
        webRequest.getRawRequest().setAttribute( "error.handled", Boolean.TRUE );
        return webResponse;
    }

    private ApiDescriptor createDynamicApiDescriptor( final Map<String, ?> properties )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( (String) properties.get( "applicationKey" ) );
        final String apiKey = Objects.requireNonNullElse( (String) properties.get( "apiKey" ), UNNAMED_API_KEY );
        final PrincipalKeys allowedPrincipals = resolveDynamicPrincipalKeys( properties.get( "allowedPrincipals" ) );

        return ApiDescriptor.create().key( DescriptorKey.from( applicationKey, apiKey ) ).allowedPrincipals( allowedPrincipals ).build();
    }

    private PrincipalKeys resolveDynamicPrincipalKeys( final Object allowedPrincipals )
    {
        return switch ( allowedPrincipals )
        {
            case null -> null;
            case String s -> PrincipalKeys.from( s );
            case String[] strings ->
                PrincipalKeys.from( Arrays.stream( strings ).map( PrincipalKey::from ).collect( Collectors.toList() ) );
            default -> throw new IllegalArgumentException( "Invalid allowedPrincipals. Value must be string or string array." );
        };
    }

    private static class UniversalApiHandlerWrapper
    {
        private final UniversalApiHandler apiHandler;

        private final ApiDescriptor apiDescriptor;

        private UniversalApiHandlerWrapper( final UniversalApiHandler apiHandler, final ApiDescriptor apiDescriptor )
        {
            this.apiHandler = apiHandler;
            this.apiDescriptor = apiDescriptor;
        }
    }
}
