package com.enonic.wem.admin.rest.resource.auth;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

@Path("auth")
public final class AuthResource
{
    @POST
    @Path("login")
    public JsonResult login()
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
