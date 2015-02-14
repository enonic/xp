package com.enonic.xp.admin.impl.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.content.PushContentException;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Provider
public final class JsonExceptionMapper
    implements ExceptionMapper<Throwable>
{
    private final static Logger LOG = LoggerFactory.getLogger( JsonExceptionMapper.class );

    @Override
    public final Response toResponse( final Throwable cause )
    {
        if ( cause instanceof WebApplicationException )
        {
            return toErrorInfo( (WebApplicationException) cause );
        }

        if ( cause instanceof NotFoundException )
        {
            return toErrorInfo( cause, Response.Status.NOT_FOUND.getStatusCode() );
        }

        if ( cause instanceof IllegalArgumentException )
        {
            return toErrorInfo( cause, Response.Status.BAD_REQUEST.getStatusCode() );
        }

        if ( cause instanceof PushContentException )
        {
            return toErrorInfo( cause, Response.Status.BAD_REQUEST.getStatusCode() );
        }

        return toErrorInfo( cause, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() );
    }

    private Response toErrorInfo( final WebApplicationException cause )
    {
        return toErrorInfo( cause, cause.getResponse().getStatus() );
    }

    private Response toErrorInfo( final Throwable cause, final int status )
    {
        logErrorIfNeeded( cause, status );
        final ObjectNode json = createErrorJson( cause, status );
        return Response.status( status ).entity( json ).type( MediaType.APPLICATION_JSON_TYPE ).build();
    }

    private ObjectNode createErrorJson( final Throwable cause, final int status )
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put( "status", status );
        node.put( "message", cause.getMessage() );
        node.set( "context", createContextJson() );
        return node;
    }

    private ObjectNode createContextJson()
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

    private void logErrorIfNeeded( final Throwable cause, final int status )
    {
        if ( !shouldLogAsError( status ) )
        {
            return;
        }

        final String message = cause.getMessage();
        LOG.error( message != null ? message : cause.getClass().getSimpleName(), cause );
    }

    private boolean shouldLogAsError( final int status )
    {
        if ( ( status >= 500 ) && ( status < 600 ) )
        {
            return true;
        }

        if ( status == 400 )
        {
            return true;
        }

        return false;
    }
}
