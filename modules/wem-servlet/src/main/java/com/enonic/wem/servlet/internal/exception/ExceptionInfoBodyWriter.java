package com.enonic.wem.servlet.internal.exception;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.servlet.jaxrs.exception.ErrorPageBuilder;
import com.enonic.wem.servlet.jaxrs.exception.ExceptionInfo;

@Provider
final class ExceptionInfoBodyWriter
    implements MessageBodyWriter<ExceptionInfo>
{
    @Override
    public boolean isWriteable( final Class<?> aClass, final Type type, final Annotation[] annotations, final MediaType mediaType )
    {
        return true;
    }

    @Override
    public long getSize( final ExceptionInfo exceptionInfo, final Class<?> aClass, final Type type, final Annotation[] annotations,
                         final MediaType mediaType )
    {
        return -1;
    }

    @Override
    public void writeTo( final ExceptionInfo exceptionInfo, final Class<?> aClass, final Type type, final Annotation[] annotations,
                         final MediaType mediaType, final MultivaluedMap<String, Object> stringObjectMultivaluedMap,
                         final OutputStream outputStream )
        throws IOException, WebApplicationException
    {
        final String output = renderInfo( mediaType, exceptionInfo );
        outputStream.write( output.getBytes( "UTF-8" ) );
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
