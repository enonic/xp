package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.serializer.WebSerializerService;
import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketContextFactory;

@Component(immediate = true, service = Filter.class, property = {"connector=api"})
@Order(100)
@WebFilter("/*")
public final class SlashApiFilter
    implements Filter
{
    private static final Logger LOG = LoggerFactory.getLogger( SlashApiFilter.class );

    private static final Pattern API_PATTERN = Pattern.compile( "^/[^/]+:[^/?]+" );

    private final SlashApiHandler slashApiHandler;

    private final ExceptionMapper exceptionMapper;

    private final WebSerializerService webSerializerService;

    private final WebSocketContextFactory webSocketContextFactory;

    @Activate
    public SlashApiFilter( @Reference final SlashApiHandler slashApiHandler, @Reference final WebSerializerService webSerializerService,
                           @Reference final WebSocketContextFactory webSocketContextFactory,
                           @Reference final ExceptionMapper exceptionMapper )
    {
        this.slashApiHandler = slashApiHandler;
        this.webSerializerService = webSerializerService;
        this.webSocketContextFactory = webSocketContextFactory;
        this.exceptionMapper = exceptionMapper;
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

        WebResponse webResponse;
        try
        {
            webResponse = slashApiHandler.handle( webRequest );
        }
        catch ( Exception e )
        {
            webResponse = toErrorResponse( e );
            req.setAttribute( "error.handled", Boolean.TRUE );
        }

        if ( webRequest.getWebSocketContext() != null && webResponse.getWebSocket() != null )
        {
            return;
        }

        webSerializerService.response( webRequest, webResponse, res );
    }

    public WebResponse toErrorResponse( final Throwable cause )
    {
        int status = exceptionMapper.map( cause ).getStatus().value();
        if ( status >= 500 )
        {
            LOG.error( Objects.requireNonNullElseGet( cause.getMessage(), cause.getClass()::getSimpleName ), cause );
        }
        else if ( LOG.isDebugEnabled() )
        {
            LOG.debug( Objects.requireNonNullElseGet( cause.getMessage(), cause.getClass()::getSimpleName ), cause );
        }

        return WebResponse.create()
            .status( HttpStatus.from( status ) )
            .body( createErrorJson( cause, status ) )
            .contentType( MediaType.JSON_UTF_8 )
            .build();
    }

    static ObjectNode createErrorJson( final Throwable cause, final int status )
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put( "status", status );
        node.put( "message", cause.getMessage() );
        node.set( "context", createContextJson() );
        return node;
    }

    private static ObjectNode createContextJson()
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authInfo = context.getAuthInfo();

        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put( "authenticated", ( authInfo != null ) && authInfo.isAuthenticated() );
        final ArrayNode principals = node.putArray( "principals" );

        if ( authInfo != null )
        {
            for ( final PrincipalKey principal : authInfo.getPrincipals() )
            {
                principals.add( principal.toString() );
            }
        }

        return node;
    }
}
