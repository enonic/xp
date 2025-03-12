package com.enonic.xp.portal.impl.handler;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
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
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

@Component(service = ServiceHandler.class)
public class ServiceHandler
{
    private static final Predicate<WebRequest> IS_STANDARD_METHOD = req -> HttpMethod.standard().contains( req.getMethod() );

    private static final Pattern PATTERN = Pattern.compile( "(?<appKey>[^/]+)/(?<serviceName>[^/]+)" );

    private final ContentService contentService;

    private final ServiceDescriptorService serviceDescriptorService;

    private final ControllerScriptFactory controllerScriptFactory;

    @Activate
    public ServiceHandler( @Reference final ContentService contentService,
                           @Reference final ServiceDescriptorService serviceDescriptorService,
                           @Reference final ControllerScriptFactory controllerScriptFactory )
    {
        this.contentService = contentService;
        this.serviceDescriptorService = serviceDescriptorService;
        this.controllerScriptFactory = controllerScriptFactory;
    }

    public WebResponse handle( final WebRequest webRequest )
        throws Exception
    {
        final String restPath = HandlerHelper.findRestPath( webRequest, "service" );
        final Matcher matcher = PATTERN.matcher( restPath );
        if ( !matcher.find() )
        {
            throw WebException.notFound( "Not a valid service url pattern" );
        }

        if ( !IS_STANDARD_METHOD.test( webRequest ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", webRequest.getMethod() ) );
        }

        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( HttpMethod.standard() );
        }

        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
        final String name = matcher.group( 2 );
        final String servicePath = matcher.group( 0 );
        final DescriptorKey descriptorKey = DescriptorKey.from( applicationKey, name );

        final PortalRequest portalRequest = createPortalRequest( webRequest, servicePath, descriptorKey );

        //Executes the service
        final ControllerScript controllerScript =
            controllerScriptFactory.fromScript( ResourceKey.from( applicationKey, "services/" + name + "/" + name + ".js" ) );

        final PortalResponse portalResponse = controllerScript.execute( portalRequest );

        final WebSocketConfig webSocketConfig = portalResponse.getWebSocket();
        final WebSocketContext webSocketContext = portalRequest.getWebSocketContext();
        if ( ( webSocketContext != null ) && ( webSocketConfig != null ) )
        {
            final WebSocketEndpoint webSocketEndpoint = newWebSocketEndpoint( webSocketConfig, controllerScript, applicationKey );
            webSocketContext.apply( webSocketEndpoint );
        }

        return portalResponse;
    }

    private PortalRequest createPortalRequest( final WebRequest webRequest, final String servicePath, final DescriptorKey descriptorKey )
    {
        final PortalRequest portalRequest =
            webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );
        portalRequest.setContextPath( HandlerHelper.findPreRestPath( portalRequest, "service" ) + "/" + servicePath );

        ContentResolver contentResolver = new ContentResolver( this.contentService );

        //Retrieves the ServiceDescriptor
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

        final ContentResolverResult resolvedContent = contentResolver.resolve( portalRequest );

        final Site site = resolvedContent.getNearestSite();

        //Checks if the application is set on the current site
        if ( site != null && site.getSiteConfigs().get( descriptorKey.getApplicationKey() ) == null )
        {
            throw WebException.forbidden( String.format( "Service [%s] forbidden for this site", descriptorKey ) );
        }

        //Checks if the application is set on the current application
        final ApplicationKey baseApplicationKey = getBaseApplicationKey( portalRequest );
        if ( baseApplicationKey != null && !baseApplicationKey.equals( descriptorKey.getApplicationKey() ) )
        {
            throw WebException.forbidden( String.format( "Service [%s] forbidden for this application", descriptorKey ) );
        }

        //Prepares the request
        portalRequest.setApplicationKey( descriptorKey.getApplicationKey() );
        portalRequest.setContent( resolvedContent.getContent() );
        portalRequest.setSite( site );

        return portalRequest;
    }

    private ApplicationKey getBaseApplicationKey( PortalRequest portalRequest )
    {
        final Matcher matcher = WebAppHandler.PATTERN.matcher( portalRequest.getRawPath() );
        if ( matcher.matches() )
        {
            final String applicationBase = matcher.group( 1 );
            return ApplicationKey.from( applicationBase );
        }
        return null;
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
}
