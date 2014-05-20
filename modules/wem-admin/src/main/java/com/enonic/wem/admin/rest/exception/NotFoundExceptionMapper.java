package com.enonic.wem.admin.rest.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.admin.rest.resource.ErrorJson;
import com.enonic.wem.api.exception.NotFoundException;

@Provider
public final class NotFoundExceptionMapper
    implements ExceptionMapper<NotFoundException>
{
    // @Context
    // protected HttpHeaders headers;

    @Override
    public Response toResponse( final NotFoundException cause )
    {
        return Response.status( Response.Status.NOT_FOUND ).type( MediaType.APPLICATION_JSON_TYPE ).entity(
            new ErrorJson( cause.getMessage() ) ).build();
    }
}
