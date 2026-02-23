package com.enonic.xp.jaxrs.impl.exception;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.WebException;

@Provider
public final class JsonExceptionMapper
    implements ExceptionMapper<Throwable>
{
    private static final Logger LOG = LoggerFactory.getLogger( JsonExceptionMapper.class );

    @Override
    public Response toResponse( final Throwable cause )
    {
        int status = switch ( cause )
        {
            case WebApplicationException wae -> wae.getResponse().getStatus();
            case WebException we -> we.getStatus().value();
            case NotFoundException _ -> Response.Status.NOT_FOUND.getStatusCode();
            default -> Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        };
        if ( status >= 500 )
        {
            LOG.error( Objects.requireNonNullElseGet( cause.getMessage(), cause.getClass()::getSimpleName ), cause );
        }
        else if ( LOG.isDebugEnabled() )
        {
            LOG.debug( Objects.requireNonNullElseGet( cause.getMessage(), cause.getClass()::getSimpleName ), cause );
        }
        return Response.status( status ).entity( createErrorJson( cause, status ) ).type( MediaType.APPLICATION_JSON_TYPE ).build();
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
