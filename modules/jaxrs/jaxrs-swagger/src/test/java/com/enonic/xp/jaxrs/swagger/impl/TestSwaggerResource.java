package com.enonic.xp.jaxrs.swagger.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.enonic.xp.jaxrs.JaxRsComponent;

@Path("/test")
@Api(value = "/test", description = "A test resource")
public class TestSwaggerResource
    implements JaxRsComponent
{
    @GET
    @ApiOperation( "Says hello" )
    public String helloWorld()
    {
        return "Hello World";
    }
}
