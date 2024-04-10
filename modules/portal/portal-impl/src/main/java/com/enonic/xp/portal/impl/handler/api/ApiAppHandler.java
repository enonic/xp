package com.enonic.xp.portal.impl.handler.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.api.ApiDescriptor;
import com.enonic.xp.portal.impl.api.ApiDescriptorService;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

@Component(immediate = true, service = WebHandler.class)
public class ApiAppHandler
    extends BaseWebHandler
{
    public static final Pattern PATTERN = Pattern.compile( "^/api/(?<appKey>(?!media|idprovider)[^/]+)/(?<restPath>.*)?$" );

    private final ControllerScriptFactory controllerScriptFactory;

    private final ApiDescriptorService apiDescriptorService;

    private final ExceptionMapper exceptionMapper;

    private final ExceptionRenderer exceptionRenderer;

    @Activate
    public ApiAppHandler( @Reference final ControllerScriptFactory controllerScriptFactory,
                          @Reference final ApiDescriptorService apiDescriptorService, @Reference final ExceptionMapper exceptionMapper,
                          @Reference final ExceptionRenderer exceptionRenderer )
    {
        this.controllerScriptFactory = controllerScriptFactory;
        this.apiDescriptorService = apiDescriptorService;
        this.exceptionMapper = exceptionMapper;
        this.exceptionRenderer = exceptionRenderer;
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return PATTERN.matcher( webRequest.getRawPath() ).matches();
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        final Matcher matcher = PATTERN.matcher( webRequest.getRawPath() );
        matcher.matches();

        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( "appKey" ) );

        checkAccessIfNeeded( applicationKey );

        final PortalRequest portalRequest = createPortalRequest( webRequest, applicationKey );

        final Trace trace = Tracer.newTrace( "handleAPI" );
        if ( trace == null )
        {
            return handleAPIRequest( portalRequest, applicationKey );
        }
        return Tracer.traceEx( trace, () -> {
            final WebResponse resp = handleAPIRequest( portalRequest, applicationKey );
            addTraceInfo( trace, resp );
            return resp;
        } );
    }

    private WebResponse handleAPIRequest( final PortalRequest portalRequest, final ApplicationKey applicationKey )
    {
        try
        {
            PortalRequestAccessor.set( portalRequest.getRawRequest(), portalRequest );

            final WebResponse returnedWebResponse = executeController( portalRequest, applicationKey );
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

    private void checkAccessIfNeeded( final ApplicationKey applicationKey )
    {
        final ApiDescriptor apiDescriptor = apiDescriptorService.getByApplication( applicationKey );
        if ( apiDescriptor != null )
        {
            final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();
            if ( !apiDescriptor.isAccessAllowed( principals ) )
            {
                throw WebException.forbidden( String.format( "You don't have permission to access API for [%s]", applicationKey ) );
            }
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

    private PortalResponse executeController( final PortalRequest req, final ApplicationKey applicationKey )
        throws Exception
    {
        final ControllerScript script = getScript( applicationKey );
        final PortalResponse res = script.execute( req );

        final WebSocketConfig webSocketConfig = res.getWebSocket();
        final WebSocketContext webSocketContext = req.getWebSocketContext();
        if ( webSocketContext != null && webSocketConfig != null )
        {
            final WebSocketEndpoint webSocketEndpoint = newWebSocketEndpoint( webSocketConfig, script, applicationKey );
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

    private ControllerScript getScript( final ApplicationKey applicationKey )
    {
        final ResourceKey script = ResourceKey.from( applicationKey, "api/api.js" );

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
}
