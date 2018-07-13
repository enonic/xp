package com.enonic.xp.portal.impl.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpMethod;
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
public final class AppHandler
    extends BaseWebHandler
{
    public final static Pattern PATTERN = Pattern.compile( "/app/([^/]+)(/(?:.)*)?" );

    private final static String ROOT_ASSET_PREFIX = "assets/";

    private ControllerScriptFactory controllerScriptFactory;

    private ExceptionMapper exceptionMapper;

    private ExceptionRenderer exceptionRenderer;

    private ResourceService resourceService;

    public AppHandler()
    {
        super( 200 );
    }

    @Override
    protected boolean canHandle( final WebRequest req )
    {
        return PATTERN.matcher( req.getRawPath() ).matches();
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse res, final WebHandlerChain chain )
        throws Exception
    {
        PortalRequest portalRequest = (PortalRequest) webRequest;
        final Matcher matcher = PATTERN.matcher( portalRequest.getRawPath() );
        matcher.matches();

        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
        final String restPath = matcher.group( 2 );

        final Trace trace = Tracer.newTrace( "renderApp" );
        if ( trace == null )
        {
            return handleAppRequest( portalRequest, applicationKey, restPath );
        }
        return Tracer.traceEx( trace, () -> {
            final WebResponse resp = handleAppRequest( portalRequest, applicationKey, restPath );
            addTraceInfo( trace, applicationKey, restPath );
            return resp;
        } );
    }

    private WebResponse handleAppRequest( final PortalRequest portalRequest, final ApplicationKey applicationKey, final String path )
    {
        if ( path != null && !"/".equals( path ) && portalRequest.getMethod() == HttpMethod.GET )
        {
            final WebResponse response = serveAsset( applicationKey, path );
            if ( response != null )
            {
                return response;
            }
        }

        return handleRequest( portalRequest );
    }

    private WebResponse handleRequest( final PortalRequest req )
    {
        try
        {
            PortalRequestAccessor.set( req.getRawRequest(), req );

            final WebResponse returnedWebResponse = executeController( req );
            exceptionMapper.throwIfNeeded( returnedWebResponse );
            return returnedWebResponse;
        }
        catch ( Exception e )
        {
            return handleError( req, e );
        }
    }

    private PortalResponse executeController( final PortalRequest req )
        throws Exception
    {
        final ControllerScript script = getScript( req.getApplicationKey() );
        final PortalResponse res = script.execute( req );

        final WebSocketConfig webSocketConfig = res.getWebSocket();
        final WebSocketContext webSocketContext = req.getWebSocketContext();
        if ( ( webSocketContext != null ) && ( webSocketConfig != null ) )
        {
            final WebSocketEndpoint webSocketEndpoint = newWebSocketEndpoint( webSocketConfig, script, req.getApplicationKey() );
            webSocketContext.apply( webSocketEndpoint );
        }

        return res;
    }

    private WebSocketEndpoint newWebSocketEndpoint( final WebSocketConfig config, final ControllerScript script, final ApplicationKey app )
    {
        final Trace trace = Tracer.current();
        if ( trace != null && !trace.containsKey( "app" ) )
        {
            trace.put( "app", app.toString() );
        }
        return new WebSocketEndpointImpl( config, () -> script );
    }

    private ControllerScript getScript( final ApplicationKey applicationKey )
    {
        final ResourceKey script = ResourceKey.from( applicationKey, "main.js" );
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

    private WebResponse serveAsset( final ApplicationKey applicationKey, final String path )
    {
        final ResourceKey key = ResourceKey.from( applicationKey, ROOT_ASSET_PREFIX + path );
        final Resource resource = this.resourceService.getResource( key );

        if ( !resource.exists() )
        {
            return null;
        }

        final String type = MediaTypes.instance().fromFile( key.getName() ).toString();
        return PortalResponse.create().
            body( resource ).
            contentType( MediaType.parse( type ) ).
            build();
    }

    private void addTraceInfo( final Trace trace, final ApplicationKey applicationKey, final String path )
    {
        if ( trace != null )
        {
            trace.put( "app", applicationKey.toString() );
            trace.put( "path", path );
        }
    }

    @Reference
    public void setControllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }

    @Reference
    public void setExceptionMapper( final ExceptionMapper exceptionMapper )
    {
        this.exceptionMapper = exceptionMapper;
    }

    @Reference
    public void setExceptionRenderer( final ExceptionRenderer exceptionRenderer )
    {
        this.exceptionRenderer = exceptionRenderer;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
