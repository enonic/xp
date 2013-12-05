package com.enonic.wem.portal.content;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("{mode}/{path:.+}")
public final class ContentResource
{
    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String path;

    @GET
    public String handleGet()
    {
        return "content, mode = " + this.mode + ", path = " + this.path;
    }
}
