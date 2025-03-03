package com.enonic.xp.jaxrs.impl;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import com.enonic.xp.jaxrs.JaxRsComponent;

@Path("/test")
public class TestResource
    implements JaxRsComponent
{
    @GET
    @Produces("text/plain")
    public String helloWorld()
    {
        return "Hello World";
    }
}
