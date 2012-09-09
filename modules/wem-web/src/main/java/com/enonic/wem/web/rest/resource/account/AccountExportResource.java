package com.enonic.wem.web.rest.resource.account;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

@Component
@Path("account/export")
public class AccountExportResource
{
    @GET
    @Path("query")
    public Response byQuery( @QueryParam("sort") @DefaultValue("") String sort, @QueryParam("dir") @DefaultValue("ASC") String sortDir,
                             @QueryParam("query") @DefaultValue("") String query,
                             @QueryParam("type") @DefaultValue("users,groups,roles") String types,
                             @QueryParam("userstores") @DefaultValue("") String userStores,
                             @QueryParam("organizations") @DefaultValue("") String organizations,
                             @QueryParam("encoding") @DefaultValue("ISO-8859-1") String characterEncoding,
                             @QueryParam("separator") @DefaultValue("\t") String separator )
    {
        // TODO: Implement
        return null;
    }

    @POST
    @Path("keys")
    public Response byKeys( @QueryParam("key") List<String> keys,
                            @QueryParam("encoding") @DefaultValue("ISO-8859-1") String characterEncoding,
                            @QueryParam("separator") @DefaultValue("\t") String separator )
    {
        // TODO: Implement
        return null;
    }
}
