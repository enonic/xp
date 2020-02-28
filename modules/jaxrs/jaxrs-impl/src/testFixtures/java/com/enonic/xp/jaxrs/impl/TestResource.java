package com.enonic.xp.jaxrs.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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