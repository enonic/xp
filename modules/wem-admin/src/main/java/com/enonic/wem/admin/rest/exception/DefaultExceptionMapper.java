package com.enonic.wem.admin.rest.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.admin.rest.resource.ErrorJson;

@Provider
public final class DefaultExceptionMapper
    implements ExceptionMapper<Throwable>
{
    private final static Logger LOG = LoggerFactory.getLogger( DefaultExceptionMapper.class );

    // @Context
    // protected HttpHeaders headers;

    @Override
    public Response toResponse( final Throwable cause )
    {
        LOG.warn( cause.getMessage(), cause );
        cause.printStackTrace();
        return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).type( MediaType.APPLICATION_JSON_TYPE ).entity(
            new ErrorJson( cause.getMessage() ) ).build();
    }
}
