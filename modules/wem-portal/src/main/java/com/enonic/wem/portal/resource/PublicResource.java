package com.enonic.wem.portal.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("{mode}/{path:.+}/_/public/{module}/{resource:.+}")
public final class PublicResource
{
    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentPath;

    @PathParam("module")
    protected String moduleName;

    @PathParam("resource")
    protected String resourceName;

    @GET
    public Response getResource()
    {
        return Response.ok().build();
    }
}
