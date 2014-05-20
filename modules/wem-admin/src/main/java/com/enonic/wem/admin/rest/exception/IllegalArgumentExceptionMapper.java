package com.enonic.wem.admin.rest.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.admin.rest.resource.ErrorJson;

@Provider
public final class IllegalArgumentExceptionMapper
    implements ExceptionMapper<IllegalArgumentException>
{
    // @Context
    // protected HttpHeaders headers;

    @Override
    public Response toResponse( final IllegalArgumentException cause )
    {
        return Response.status( Response.Status.BAD_REQUEST ).type( MediaType.APPLICATION_JSON_TYPE ).entity(
            new ErrorJson( cause.getMessage() ) ).build();
    }
}
