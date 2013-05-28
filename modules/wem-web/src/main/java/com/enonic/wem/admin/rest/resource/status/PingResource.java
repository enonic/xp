package com.enonic.wem.admin.rest.resource.status;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

@Path("status/ping")
public final class PingResource
{
    @GET
    public JsonResult ping()
    {
        return new JsonResult( true )
        {
            @Override
            protected void serialize( final ObjectNode json )
            {
            }
        };
    }
}
