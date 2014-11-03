package com.enonic.wem.servlet.internal.exception;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.servlet.jaxrs.exception.ExceptionInfo;

@Provider
final class ExceptionInfoFilter
    implements ContainerResponseFilter
{
    @Context
    private HttpHeaders headers;

    @Override
    public void filter( final ContainerRequestContext req, final ContainerResponseContext res )
        throws IOException
    {
        final Object entity = res.getEntity();
        if ( !( entity instanceof ExceptionInfo ) )
        {
            return;
        }

        final MediaType type = findMediaType();
        res.setEntity( renderInfo( type, (ExceptionInfo) entity ), null, type );
    }

    private MediaType findMediaType()
    {
        final List<MediaType> list = this.headers.getAcceptableMediaTypes();
        for ( final MediaType type : list )
        {
            if ( !type.isWildcardType() && type.isCompatible( MediaType.TEXT_HTML_TYPE ) )
            {
                return MediaType.TEXT_HTML_TYPE;
            }
        }

        return MediaType.APPLICATION_JSON_TYPE;
    }


    private String renderInfo( final MediaType mediaType, final ExceptionInfo info )
    {
        if ( mediaType.isCompatible( MediaType.TEXT_HTML_TYPE ) )
        {
            return renderInfo( info, info.getCause() );
        }

        return renderJson( info, info.getCause() ).toString();
    }

    private String renderInfo( final ExceptionInfo info, final Throwable cause )
    {
        final ErrorPageBuilder builder = new ErrorPageBuilder().
            cause( cause ).
            description( getDescription( info, cause ) ).
            status( info.getStatus() ).
            title( info.getReasonPhrase() );

        return builder.build();
    }

    private ObjectNode renderJson( final ExceptionInfo info, final Throwable cause )
    {
        final ObjectNode root = JsonNodeFactory.instance.objectNode();
        root.put( "status", info.getStatus() );
        root.put( "message", info.getMessage() );

        if ( cause != null )
        {
            root.put( "cause", cause.getClass().getName() );
        }

        return root;
    }

    public String getDescription( final ExceptionInfo info, final Throwable cause )
    {
        String str = info.getMessage();
        if ( cause != null )
        {
            str += " (" + cause.getClass().getName() + ")";
        }

        return str;
    }
}
