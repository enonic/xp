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
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.util.MediaTypes;
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
    private final static Pattern PATTERN = Pattern.compile( "/app/([^/]+)(.+)?" );

    private final static String ROOT_ASSET_PREFIX = "assets/";

    private ControllerScriptFactory controllerScriptFactory;

    private ExceptionMapper exceptionMapper;

    private ExceptionRenderer exceptionRenderer;

    private ResourceService resourceService;

    public AppHandler()
    {
        super( -40 );
    }

    @Override
    protected boolean canHandle( final WebRequest req )
    {
        return PATTERN.matcher( req.getRawPath() ).matches();
    }

    @Override
    protected WebResponse doHandle( final WebRequest req, final WebResponse res, final WebHandlerChain chain )
        throws Exception
    {
        final Matcher matcher = PATTERN.matcher( req.getRawPath() );
        if ( !matcher.matches() )
        {
            return chain.handle( req, res );
        }

        final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
        final String restPath = matcher.group( 2 );

        final WebResponse response = serveAsset( applicationKey, restPath );
        if ( response != null )
        {
            return response;
        }

        return executeController( req, applicationKey );
    }

    private WebResponse executeController( final WebRequest req, final ApplicationKey applicationKey )
        throws Exception
    {
        final PortalRequest portalRequest = createRequest( req, applicationKey );

        try
        {
            PortalRequestAccessor.set( portalRequest.getRawRequest(), portalRequest );

            final WebResponse returnedWebResponse = doHandle( portalRequest );
            exceptionMapper.throwIfNeeded( returnedWebResponse );
            return returnedWebResponse;
        }
        catch ( Exception e )
        {
            return handleError( portalRequest, e );
        }
    }

    private PortalRequest createRequest( final WebRequest req, final ApplicationKey applicationKey )
    {
        final PortalRequest portalRequest = ( req instanceof PortalRequest ) ? (PortalRequest) req : new PortalRequest( req );
        portalRequest.setMode( RenderMode.APP );
        portalRequest.setApplicationKey( applicationKey );
        portalRequest.setBaseUri( "/app/" + applicationKey.getName() );

        return portalRequest;
    }

    private PortalResponse doHandle( final PortalRequest req )
        throws Exception
    {
        final ControllerScript script = getScript( req.getApplicationKey() );
        final PortalResponse res = script.execute( req );

        final WebSocketConfig webSocketConfig = res.getWebSocket();
        final WebSocketContext webSocketContext = req.getWebSocketContext();
        if ( ( webSocketContext != null ) && ( webSocketConfig != null ) )
        {
            final WebSocketEndpoint webSocketEndpoint = newWebSocketEndpoint( webSocketConfig, script );
            webSocketContext.apply( webSocketEndpoint );
        }

        return res;
    }

    private WebSocketEndpoint newWebSocketEndpoint( final WebSocketConfig config, final ControllerScript script )
    {
        return new WebSocketEndpointImpl( config, script );
    }

    private ControllerScript getScript( final ApplicationKey applicationKey )
    {
        return this.controllerScriptFactory.fromScript( ResourceKey.from( applicationKey, "main.js" ) );
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
