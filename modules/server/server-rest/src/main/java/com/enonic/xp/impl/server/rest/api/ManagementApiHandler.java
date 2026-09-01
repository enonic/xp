package com.enonic.xp.impl.server.rest.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * Base for the management APIs: a resource exposed on the management endpoint as {@code /<application>:<api>} with a
 * fixed set of routes, each bound to a verb ({@code list}, {@code create}, {@code prune}, ...). Several routes may map to
 * the same verb - that is how a deprecated path stays an alias of its replacement. Aliases resolve to the canonical
 * verb before the {@link ManagementApiPolicy} check, so an alias can never reach an operation its verb would not.
 */
public abstract class ManagementApiHandler
    implements UniversalApiHandler
{
    protected static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final Pattern PLACEHOLDER = Pattern.compile( "\\{([a-zA-Z]+)}" );

    private final String descriptorKey;

    private final List<Route> routes = new ArrayList<>();

    protected ManagementApiHandler( final String descriptorKey )
    {
        this.descriptorKey = descriptorKey;
    }

    public final String getDescriptorKey()
    {
        return descriptorKey;
    }

    /**
     * Registers a route. {@code pattern} is a path relative to the API root, {@code "/"} being the root itself; a
     * {@code {name}} segment captures a path parameter.
     */
    protected final void route( final HttpMethod method, final String pattern, final String verb, final RouteHandler handler )
    {
        routes.add( new Route( method, pattern, verb, handler ) );
    }

    @Override
    public WebResponse handle( final WebRequest request )
    {
        final String apiPath = normalize( WebHandlerHelper.findApiPath( request, descriptorKey ) );

        boolean pathMatched = false;
        for ( final Route route : routes )
        {
            final Map<String, String> pathParams = route.match( apiPath );
            if ( pathParams == null )
            {
                continue;
            }
            pathMatched = true;
            if ( route.method != request.getMethod() )
            {
                continue;
            }

            final ManagementApiPolicy policy = ManagementApiPolicy.of( descriptorKey );
            if ( !policy.allows( route.verb ) )
            {
                return error( HttpStatus.FORBIDDEN,
                              String.format( "Verb [%s] of API [%s] is not exposed on this virtual host", route.verb, descriptorKey ) );
            }

            try
            {
                return route.handler.handle( request, pathParams );
            }
            catch ( JsonProcessingException | IllegalArgumentException e )
            {
                return error( HttpStatus.BAD_REQUEST, e.getMessage() );
            }
        }

        return pathMatched
            ? error( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", request.getMethod() ) )
            : error( HttpStatus.NOT_FOUND, String.format( "Path [%s] not found in API [%s]", apiPath, descriptorKey ) );
    }

    protected final WebResponse json( final Object body )
        throws JsonProcessingException
    {
        return json( HttpStatus.OK, body );
    }

    protected final WebResponse json( final HttpStatus status, final Object body )
        throws JsonProcessingException
    {
        return WebResponse.create().status( status ).contentType( MediaType.JSON_UTF_8 ).body( MAPPER.writeValueAsString( body ) ).build();
    }

    /**
     * The response of every long-running command: {@code 202 Accepted} with the id of the task to poll.
     */
    protected final WebResponse accepted( final TaskId taskId )
        throws JsonProcessingException
    {
        return json( HttpStatus.ACCEPTED, new TaskResultJson( taskId ) );
    }

    protected final WebResponse error( final HttpStatus status, final String message )
    {
        try
        {
            return json( status, Map.of( "status", status.value(), "message", message == null ? status.getReasonPhrase() : message ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new IllegalStateException( e );
        }
    }

    protected final <T> T body( final WebRequest request, final Class<T> type )
        throws JsonProcessingException
    {
        final String body = request.getBodyAsString();
        if ( body == null || body.isBlank() )
        {
            throw new IllegalArgumentException( "Request body is required" );
        }
        return MAPPER.readValue( body, type );
    }

    protected static String param( final WebRequest request, final String name )
    {
        return request.getParams().get( name ).stream().findFirst().orElse( null );
    }

    private static String normalize( final String apiPath )
    {
        if ( apiPath == null || apiPath.isEmpty() )
        {
            return "/";
        }
        return apiPath.length() > 1 && apiPath.endsWith( "/" ) ? apiPath.substring( 0, apiPath.length() - 1 ) : apiPath;
    }

    @FunctionalInterface
    protected interface RouteHandler
    {
        WebResponse handle( WebRequest request, Map<String, String> pathParams )
            throws JsonProcessingException;
    }

    private static final class Route
    {
        private final HttpMethod method;

        private final String verb;

        private final RouteHandler handler;

        private final Pattern pattern;

        private final List<String> names = new ArrayList<>();

        private Route( final HttpMethod method, final String pattern, final String verb, final RouteHandler handler )
        {
            this.method = method;
            this.verb = verb;
            this.handler = handler;
            this.pattern = compile( pattern );
        }

        private Pattern compile( final String pattern )
        {
            final Matcher matcher = PLACEHOLDER.matcher( pattern );
            final StringBuilder regex = new StringBuilder( "^" );
            int last = 0;
            while ( matcher.find() )
            {
                regex.append( Pattern.quote( pattern.substring( last, matcher.start() ) ) ).append( "([^/]+)" );
                names.add( matcher.group( 1 ) );
                last = matcher.end();
            }
            regex.append( Pattern.quote( pattern.substring( last ) ) ).append( "$" );
            return Pattern.compile( regex.toString() );
        }

        private Map<String, String> match( final String path )
        {
            final Matcher matcher = pattern.matcher( path );
            if ( !matcher.matches() )
            {
                return null;
            }
            final Map<String, String> params = new LinkedHashMap<>();
            for ( int i = 0; i < names.size(); i++ )
            {
                params.put( names.get( i ), matcher.group( i + 1 ) );
            }
            return params;
        }
    }
}
