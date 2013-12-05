package com.enonic.wem.portal.services;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.HttpContext;

@Path("{mode}/{path:.+}/_/services/{module}/{script}")
public final class ServicesResource
{
    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentPath;

    @PathParam("module")
    protected String moduleName;

    @PathParam("script")
    protected String scriptName;

    @Context
    protected HttpContext httpContext;

    @GET
    public Response handleGet()
    {
        return doHandle();
    }

    @POST
    public Response handlePost()
    {
        return doHandle();
    }

    @OPTIONS
    public Response handleOptions()
    {
        return doHandle();
    }

    private Response doHandle()
    {
        return null;
    }
}
