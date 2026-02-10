package com.enonic.xp.portal.impl.app;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.handler.PathMatchers;
import com.enonic.xp.portal.impl.websocket.WebSocketEndpointImpl;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.HttpStatus;
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
public final class WebAppHandler
    extends BaseWebHandler
{
    private ControllerScriptFactory controllerScriptFactory;

    private ExceptionMapper exceptionMapper;

    private ExceptionRenderer exceptionRenderer;

    public WebAppHandler()
    {
        super( 200 );
    }

    @Override
    protected boolean canHandle( final WebRequest req )
    {
        return req instanceof PortalRequest portalRequest && portalRequest.getBaseUri().startsWith( PathMatchers.WEBAPP_PREFIX );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse res, final WebHandlerChain chain )
        throws Exception
    {
        final PortalRequest portalRequest = (PortalRequest) webRequest;
        portalRequest.setContextPath( portalRequest.getBaseUri() );

        final String restPath = portalRequest.getRawPath().substring( portalRequest.getBaseUri().length() );
        if ( restPath.isEmpty() )
        {
            return handleRedirect( webRequest );
        }
        final Trace trace = Tracer.newTrace( "renderApp" );
        if ( trace == null )
        {
            return handleRequest( portalRequest );
        }
        return Tracer.trace( trace, () -> {
            final WebResponse resp = handleRequest( portalRequest );
            addTraceInfo( trace, portalRequest.getApplicationKey(), restPath );
            return resp;
        } );
    }

    private WebResponse handleRequest( final PortalRequest req )
    {
        try
        {
            final WebResponse returnedWebResponse = executeController( req );
            exceptionMapper.throwIfNeeded( returnedWebResponse );
            return returnedWebResponse;
        }
        catch ( Exception e )
        {
            return handleError( req, e );
        }
    }

    private WebResponse handleRedirect( WebRequest webRequest )
    {
        String redirectUrl = webRequest.getPath() + "/";

        final String queryString = webRequest.getRawRequest().getQueryString();
        if ( queryString != null )
        {
            redirectUrl = redirectUrl + "?" + queryString;
        }

        return WebResponse.create().status( HttpStatus.TEMPORARY_REDIRECT ).header( HttpHeaders.LOCATION, redirectUrl ).build();
    }

    private PortalResponse executeController( final PortalRequest req )
        throws Exception
    {
        final ApplicationKey applicationKey = req.getApplicationKey();
        final ControllerScript script = getScript( applicationKey );
        final PortalResponse res = script.execute( req );

        final WebSocketConfig webSocketConfig = res.getWebSocket();
        final WebSocketContext webSocketContext = req.getWebSocketContext();
        if ( ( webSocketContext != null ) && ( webSocketConfig != null ) )
        {
            final WebSocketEndpoint webSocketEndpoint = newWebSocketEndpoint( webSocketConfig, script, applicationKey );
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
        final ResourceKey script = ResourceKey.from( applicationKey, "/webapp/webapp.js" );
        return this.controllerScriptFactory.fromScript( script );
    }

    private WebResponse handleError( final WebRequest webRequest, final Exception e )
    {
        final WebException webException = exceptionMapper.map( e );
        final WebResponse webResponse = exceptionRenderer.render( webRequest, webException );
        webRequest.getRawRequest().setAttribute( "error.handled", Boolean.TRUE );
        return webResponse;
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

}
