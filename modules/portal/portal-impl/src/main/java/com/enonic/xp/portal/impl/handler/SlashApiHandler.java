package com.enonic.xp.portal.impl.handler;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.api.ApiMount;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.site.Site;
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

@Component(service = SlashApiHandler.class)
public class SlashApiHandler
{
    private static final Predicate<WebRequest> IS_STANDARD_METHOD = req -> HttpMethod.standard().contains( req.getMethod() );

    private static final Pattern API_PATTERN = Pattern.compile( "^/(_|api)/(?<appKey>[^/]+)(?:/(?<apiKey>[^/]+))?(?<restPath>/.*)?$" );

    private static final Pattern MOUNT_SITE_ENDPOINT_PATTERN =
        Pattern.compile( "^/(admin/)?site/(?<project>[^/]+)/(?<branch>[^/]+)(?<contentPath>/.*?)/_/" );

    private static final Pattern MOUNT_WEBAPP_ENDPOINT_PATTERN = Pattern.compile( "^/webapp/(?<baseAppKey>[^/]+)(?<restPath>/.*?)/_/" );

    private static final List<String> RESERVED_APP_KEYS =
        List.of( "attachment", "image", "error", "idprovider", "service", "asset", "component", "widgets", "media" );

    private final ControllerScriptFactory controllerScriptFactory;

    private final ApiDescriptorService apiDescriptorService;

    private final ContentService contentService;

    private final ProjectService projectService;

    private final ExceptionMapper exceptionMapper;

    private final ExceptionRenderer exceptionRenderer;

    @Activate
    public SlashApiHandler( @Reference final ControllerScriptFactory controllerScriptFactory,
                            @Reference final ApiDescriptorService apiDescriptorService, @Reference final ContentService contentService,
                            @Reference final ProjectService projectService, @Reference final ExceptionMapper exceptionMapper,
                            @Reference final ExceptionRenderer exceptionRenderer )
    {
        this.controllerScriptFactory = controllerScriptFactory;
        this.apiDescriptorService = apiDescriptorService;
        this.contentService = contentService;
        this.projectService = projectService;
        this.exceptionMapper = exceptionMapper;
        this.exceptionRenderer = exceptionRenderer;
    }

    public WebResponse handle( final WebRequest webRequest )
        throws Exception
    {
        final String path = Objects.requireNonNullElse( webRequest.getEndpointPath(), webRequest.getRawPath() );
        Matcher matcher = API_PATTERN.matcher( path );
        if ( !matcher.matches() )
        {
            throw new IllegalStateException( "Invalid API path: " + path );
        }

        final ApplicationKey applicationKey = resolveApplicationKey( matcher.group( "appKey" ) );

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

        final PortalRequest portalRequest = createPortalRequest( webRequest, applicationKey );

        final String apiKey = Objects.requireNonNullElse( matcher.group( "apiKey" ), "api" );

        final String restPath = matcher.group( "restPath" );

        final ApiDescriptor apiDescriptor = resolveApiDescriptor( applicationKey, apiKey );

        if ( !verifyRequestMounted( apiDescriptor, portalRequest ) )
        {
            throw WebException.notFound( String.format( "API [%s] is not mounted", apiDescriptor.key() ) );
        }

        final Trace trace = Tracer.newTrace( "slashAPI" );
        if ( trace == null )
        {
            return handleAPIRequest( portalRequest, apiDescriptor );
        }
        return Tracer.traceEx( trace, () -> {
            final WebResponse response = handleAPIRequest( portalRequest, apiDescriptor );
            addTranceInfo( trace, applicationKey, apiKey, restPath, response );
            return response;
        } );
    }

    private boolean verifyRequestMounted( final ApiDescriptor apiDescriptor, final PortalRequest portalRequest )
    {
        final String rawPath = portalRequest.getRawPath();
        final Set<ApiMount> mounts = apiDescriptor.getMounts();

        if ( portalRequest.getEndpointPath() == null && rawPath.startsWith( "/api/" ) )
        {
            return mounts.contains( ApiMount.API );
        }
        else if ( portalRequest.getEndpointPath() != null && rawPath.startsWith( "/admin/tool/" ) )
        {
            return mounts.contains( ApiMount.ADMIN );
        }
        else if ( portalRequest.getEndpointPath() != null && rawPath.startsWith( "/site/" ) || rawPath.startsWith( "/admin/site/" ) )
        {
            return verifyRequestMountedOnSites( apiDescriptor, portalRequest );
        }
        else if ( portalRequest.getEndpointPath() != null && rawPath.startsWith( "/webapp/" ) )
        {
            return verifyPathMountedOnWebapps( rawPath, apiDescriptor );
        }
        else
        {
            return false;
        }
    }

    private boolean verifyPathMountedOnWebapps( final String rawPath, final ApiDescriptor apiDescriptor )
    {
        final Matcher webappMatcher = MOUNT_WEBAPP_ENDPOINT_PATTERN.matcher( rawPath );
        if ( !webappMatcher.find() )
        {
            return false;
        }

        if ( apiDescriptor.hasMount( ApiMount.ALL_WEBAPPS ) )
        {
            return true;
        }

        if ( !apiDescriptor.hasMount( ApiMount.WEBAPP ) )
        {
            return false;
        }

        final ApplicationKey baseAppKey = resolveApplicationKey( webappMatcher.group( "baseAppKey" ) );
        return baseAppKey.equals( apiDescriptor.key().getApplicationKey() );
    }

    private boolean verifyRequestMountedOnSites( final ApiDescriptor apiDescriptor, final PortalRequest portalRequest )
    {
        final Matcher siteMatcher = MOUNT_SITE_ENDPOINT_PATTERN.matcher( portalRequest.getRawPath() );
        if ( !siteMatcher.find() )
        {
            return false;
        }

        final ContentPath contentPath = ContentPath.from( siteMatcher.group( "contentPath" ) );

        final Site site = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( portalRequest.getRepositoryId() )
            .branch( portalRequest.getBranch() )
            .build()
            .callWith( () -> contentService.findNearestSiteByPath( contentPath ) );

        if ( apiDescriptor.hasMount( ApiMount.ALL_SITES ) )
        {
            return site != null;
        }

        if ( !apiDescriptor.hasMount( ApiMount.SITE ) )
        {
            return false;
        }

        final boolean isAppInstalledOnSite = site != null && site.getSiteConfigs().get( apiDescriptor.key().getApplicationKey() ) != null;

        if ( !isAppInstalledOnSite )
        {
            Project project = projectService.get( ProjectName.from( portalRequest.getRepositoryId() ) );
            return project != null && project.getSiteConfigs().get( apiDescriptor.key().getApplicationKey() ) != null;
        }

        return true;
    }

    private static void addTranceInfo( final Trace trace, final ApplicationKey applicationKey, final String apiKey, final String restPath,
                                       final WebResponse response )
    {
        trace.put( "app", applicationKey.toString() );
        trace.put( "api", apiKey );
        trace.put( "path", restPath );
        HandlerHelper.addTraceInfo( trace, response );
    }

    private WebResponse handleAPIRequest( final PortalRequest portalRequest, final ApiDescriptor apiDescriptor )
    {
        try
        {
            PortalRequestAccessor.set( portalRequest.getRawRequest(), portalRequest );

            final WebResponse returnedWebResponse = executeController( portalRequest, apiDescriptor );
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

    private ApiDescriptor resolveApiDescriptor( final ApplicationKey applicationKey, final String apiKey )
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, apiKey );
        final ApiDescriptor apiDescriptor = apiDescriptorService.getByKey( descriptorKey );
        if ( apiDescriptor == null )
        {
            throw WebException.notFound( String.format( "API [%s] not found", descriptorKey ) );
        }

        final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();
        if ( !apiDescriptor.isAccessAllowed( principals ) )
        {
            throw WebException.forbidden(
                String.format( "You don't have permission to access \"%s\" API for \"%s\"", apiKey, applicationKey ) );
        }

        return apiDescriptor;
    }

    private PortalRequest createPortalRequest( final WebRequest webRequest, final ApplicationKey applicationKey )
    {
        final PortalRequest portalRequest =
            webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );

        portalRequest.setContextPath( portalRequest.getBaseUri() );
        portalRequest.setApplicationKey( applicationKey );

        return portalRequest;
    }

    private PortalResponse executeController( final PortalRequest req, final ApiDescriptor apiDescriptor )
        throws Exception
    {
        final ControllerScript script = getScript( apiDescriptor );
        final PortalResponse res = script.execute( req );

        final WebSocketConfig webSocketConfig = res.getWebSocket();
        final WebSocketContext webSocketContext = req.getWebSocketContext();
        if ( webSocketContext != null && webSocketConfig != null )
        {
            final WebSocketEndpoint webSocketEndpoint =
                newWebSocketEndpoint( webSocketConfig, script, apiDescriptor.key().getApplicationKey() );
            webSocketContext.apply( webSocketEndpoint );
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

    private ControllerScript getScript( final ApiDescriptor apiDescriptor )
    {
        final ResourceKey script = apiDescriptor.toResourceKey( "js" );
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

    private ApplicationKey resolveApplicationKey( final String appKey )
    {
        try
        {
            return ApplicationKey.from( appKey );
        }
        catch ( Exception e )
        {
            throw WebException.notFound( String.format( "Application key [%s] not found", appKey ) );
        }
    }
}
