package com.enonic.xp.jaxrs.impl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.enonic.xp.jaxrs.JaxRsComponent;

@Provider
public class TestErrorHandler
    implements ExceptionMapper<WebApplicationException>, JaxRsComponent
{
    @Override
    public Response toResponse( final WebApplicationException exception )
    {
        return exception.getResponse();
    }
}
