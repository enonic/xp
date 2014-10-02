package com.enonic.wem.servlet.internal;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class DefaultExceptionMapper
    implements ExceptionMapper<Throwable>
{
    @Override
    public Response toResponse( final Throwable cause )
    {
        if ( cause instanceof WebApplicationException )
        {
            return toResponse( (WebApplicationException) cause );
        }

        return Response.serverError().entity( cause.getMessage() ).build();
    }

    private Response toResponse( final WebApplicationException cause )
    {
        return cause.getResponse();
    }
}
