package com.enonic.wem.admin.rest.resource.status;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("status")
public final class StatusResource
{
    @GET
    @Produces("application/json")
    public StatusResult getStatus()
    {
        return new StatusResult();
    }
}
