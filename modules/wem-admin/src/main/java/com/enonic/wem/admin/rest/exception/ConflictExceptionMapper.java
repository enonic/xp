package com.enonic.wem.admin.rest.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.wem.api.exception.ConflictException;
import com.enonic.wem.servlet.jaxrs.exception.ExceptionInfo;

@Provider
public final class ConflictExceptionMapper
    implements ExceptionMapper<ConflictException>
{
    @Override
    public Response toResponse( final ConflictException cause )
    {
        return ExceptionInfo.create( Response.Status.CONFLICT ).cause( cause ).toResponse();
    }
}
