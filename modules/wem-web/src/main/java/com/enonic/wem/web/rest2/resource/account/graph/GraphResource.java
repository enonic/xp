package com.enonic.wem.web.rest2.resource.account.graph;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

@Path("account/graph")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class GraphResource
{
    @GET
    @Path("{key}")
    public GraphResult getInfo( @PathParam("key") final String key )
    {
        // TODO: Implementation here.
        return null;
    }
}
