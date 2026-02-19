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

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.serializer.WebSerializerService;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

@Component(immediate = true, service = Filter.class, property = {"connector=api"})
@Order(100)
@WebFilter("/*")
public final class SlashApiServlet
    implements Filter
{
    private static final Pattern API_PATTERN = Pattern.compile( "^/[^/]+:[^/?]+" );

    private final SlashApiHandler slashApiHandler;

    private final ExceptionMapper exceptionMapper;

    private final ExceptionRenderer exceptionRenderer;

    private final WebSerializerService webSerializerService;

    private final WebSocketContextFactory webSocketContextFactory;

    @Activate
    public SlashApiServlet( @Reference final SlashApiHandler slashApiHandler, @Reference final ExceptionMapper exceptionMapper,
                            @Reference final ExceptionRenderer exceptionRenderer,
                            @Reference final WebSerializerService webSerializerService,
                            @Reference final WebSocketContextFactory webSocketContextFactory )
    {
        this.slashApiHandler = slashApiHandler;
        this.exceptionMapper = exceptionMapper;
        this.exceptionRenderer = exceptionRenderer;
        this.webSerializerService = webSerializerService;
        this.webSocketContextFactory = webSocketContextFactory;
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

        final WebRequest webRequest = webSerializerService.request( req );
        final WebSocketContext webSocketContext = this.webSocketContextFactory.newContext( req, res );
        webRequest.setWebSocketContext( webSocketContext );

        final WebResponse webResponse = doHandle( webRequest );

        if ( webRequest.getWebSocketContext() != null && webResponse.getWebSocket() != null )
        {
            return;
        }

        webSerializerService.response( webRequest, webResponse, res );
    }

    private WebResponse doHandle( final WebRequest webRequest )
    {
        try
        {
            return slashApiHandler.handle( webRequest );
        }
        catch ( Exception e )
        {
            return handleError( webRequest, e );
        }
    }

    private WebResponse handleError( final WebRequest webRequest, final Exception cause )
    {
        final WebException exception = this.exceptionMapper.map( cause );
        return this.exceptionRenderer.render( webRequest, exception );
    }
}
