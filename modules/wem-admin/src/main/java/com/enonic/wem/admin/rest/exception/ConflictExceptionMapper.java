package com.enonic.wem.admin.rest.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.admin.rest.resource.ErrorJson;
import com.enonic.wem.api.exception.ConflictException;

@Provider
public final class ConflictExceptionMapper
    implements ExceptionMapper<ConflictException>
{
    @Override
    public Response toResponse( final ConflictException cause )
    {
        return Response.status( Response.Status.CONFLICT ).type( MediaType.APPLICATION_JSON_TYPE ).entity(
            new ErrorJson( cause.getMessage() ) ).build();
    }
}
