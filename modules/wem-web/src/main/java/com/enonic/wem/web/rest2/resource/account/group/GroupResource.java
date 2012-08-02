package com.enonic.wem.web.rest2.resource.account.group;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

@Path("account/group")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class GroupResource
{
    @GET
    @Path("{key}")
    public GroupResult getInfo( @PathParam("key") final String key )
    {
        // TODO: Implementation here. Do not implement "account graph" since this will be implemented elsewhere
        return null;
    }
}
