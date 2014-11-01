package com.enonic.wem.servlet.internal.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.api.exception.NotFoundException;
import com.enonic.wem.servlet.jaxrs.exception.ExceptionInfo;

@Provider
final class DefaultExceptionMapper
    implements ExceptionMapper<Throwable>
{
    @Override
    public final Response toResponse( final Throwable cause )
    {
        final ExceptionInfo info = toErrorInfo( cause );
        return Response.status( info.getStatus() ).entity( info ).build();
    }

    private ExceptionInfo toErrorInfo( final Throwable cause )
    {
        if ( cause instanceof WebApplicationException )
        {
            return toErrorInfo( (WebApplicationException) cause );
        }

        if ( cause instanceof NotFoundException )
        {
            return ExceptionInfo.create( Response.Status.NOT_FOUND.getStatusCode() ).cause( cause );
        }

        if ( cause instanceof IllegalArgumentException )
        {
            return ExceptionInfo.create( Response.Status.BAD_REQUEST.getStatusCode() ).cause( cause );
        }

        return ExceptionInfo.create( Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() ).cause( cause );
    }

    private ExceptionInfo toErrorInfo( final WebApplicationException cause )
    {
        return ExceptionInfo.create( cause.getResponse().getStatus() ).cause( cause );
    }
}
