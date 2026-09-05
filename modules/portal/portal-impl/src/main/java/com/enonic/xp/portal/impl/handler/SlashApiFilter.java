package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.serializer.WebSerializerService;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

@Component(immediate = true, service = Filter.class, property = {"connector=api"})
@Order(100)
@WebFilter("/*")
public final class SlashApiFilter
    implements Filter
{
    private static final Pattern API_PATTERN = Pattern.compile( "^/[^/]+:[^/?]+" );

    private final SlashApiHandler slashApiHandler;

    private final ExceptionRenderer exceptionRenderer;

    private final WebSerializerService webSerializerService;

    private final WebSocketContextFactory webSocketContextFactory;

    @Activate
    public SlashApiFilter( @Reference final SlashApiHandler slashApiHandler, @Reference final WebSerializerService webSerializerService,
                           @Reference final WebSocketContextFactory webSocketContextFactory,
                           @Reference final ExceptionRenderer exceptionRenderer )
    {
        this.slashApiHandler = slashApiHandler;
        this.webSerializerService = webSerializerService;
        this.webSocketContextFactory = webSocketContextFactory;
        this.exceptionRenderer = exceptionRenderer;
    }

    @Override
    public void doFilter( final ServletRequest request, final ServletResponse response, final FilterChain chain )
        throws IOException, ServletException
    {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;

        final String pathInfo = req.getPathInfo();
        if ( pathInfo == null || !API_PATTERN.matcher( pathInfo ).find() )
        {
            chain.doFilter( request, response );
            return;
        }

        WebRequest webRequest = null;
        WebResponse webResponse;
        try
        {
            webRequest = webSerializerService.request( req );
            webRequest.setBody( readBody( req ) );
            final WebSocketContext webSocketContext = this.webSocketContextFactory.newContext( req, res );
            webRequest.setWebSocketContext( webSocketContext );

            webResponse = slashApiHandler.handle( webRequest );
        }
        catch ( Exception e )
        {
            if ( webRequest == null )
            {
                webRequest = new WebRequest();
                webRequest.setRawRequest( req );
            }
            webResponse = exceptionRenderer.render( webRequest, e );
        }

        if ( webRequest.getWebSocketContext() != null && webResponse.getWebSocket() != null )
        {
            return;
        }

        webSerializerService.response( webRequest, webResponse, res );
    }

    private static String readBody( final HttpServletRequest req )
        throws IOException
    {
        final MediaType mediaType = parseMediaType( req.getContentType() );
        return mediaType != null && ( mediaType.is( MediaType.ANY_TEXT_TYPE ) || mediaType.is( MediaType.JSON_UTF_8.withoutParameters() ) )
            ? CharStreams.toString( req.getReader() )
            : null;
    }

    private static MediaType parseMediaType( final String contentType )
    {
        if ( contentType == null )
        {
            return null;
        }
        try
        {
            return MediaType.parse( contentType );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
    }
}
