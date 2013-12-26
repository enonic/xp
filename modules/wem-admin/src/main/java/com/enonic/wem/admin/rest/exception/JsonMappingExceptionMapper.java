package com.enonic.wem.admin.rest.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonMappingException;

import com.enonic.wem.admin.rest.resource.ErrorJson;

@Provider
public final class JsonMappingExceptionMapper
    implements ExceptionMapper<JsonMappingException>
{
    @Override
    public Response toResponse( final JsonMappingException cause )
    {
        return Response.status( Response.Status.BAD_REQUEST ).type( MediaType.APPLICATION_JSON_TYPE ).entity(
            new ErrorJson( cause.getMessage() ) ).build();
    }
}
