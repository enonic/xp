package com.enonic.xp.jaxrs.impl;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

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
