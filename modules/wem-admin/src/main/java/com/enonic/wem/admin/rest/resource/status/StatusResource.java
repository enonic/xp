package com.enonic.wem.admin.rest.resource.status;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("status")
public final class StatusResource
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StatusResult getStatus()
    {
        return new StatusResult();
    }
}
