package com.enonic.wem.web.rest2.resource.account;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

@Path("account")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class AccountResource
{
    @GET
    public AccountsResult search( @QueryParam("start") @DefaultValue("0") int start, @QueryParam("limit") @DefaultValue("10") int limit,
                                  @QueryParam("sort") @DefaultValue("") String sort, @QueryParam("dir") @DefaultValue("ASC") String sortDir,
                                  @QueryParam("query") @DefaultValue("") String query,
                                  @QueryParam("type") @DefaultValue("users,groups,roles") String types,
                                  @QueryParam("userstores") @DefaultValue("") String userStores,
                                  @QueryParam("organizations") @DefaultValue("") String organizations )
    {
        // TODO: Fill inn implementation code here.
        return new AccountsResult();
    }

    @GET
    @Path("export-query")
    public Response exportQuery( @QueryParam("sort") @DefaultValue("") String sort, @QueryParam("dir") @DefaultValue("ASC") String sortDir,
                                 @QueryParam("query") @DefaultValue("") String query,
                                 @QueryParam("type") @DefaultValue("users,groups,roles") String types,
                                 @QueryParam("userstores") @DefaultValue("") String userStores,
                                 @QueryParam("organizations") @DefaultValue("") String organizations,
                                 @QueryParam("encoding") @DefaultValue("ISO-8859-1") String characterEncoding,
                                 @QueryParam("separator") @DefaultValue("t") String separator )
    {
        // TODO: Fill inn implementation code here.
        return null;
    }

    @GET
    @Path("export-keys")
    public Response exportKeys( @QueryParam("key") List<String> keys,
                                @QueryParam("encoding") @DefaultValue("ISO-8859-1") String characterEncoding,
                                @QueryParam("separator") @DefaultValue("t") String separator )
    {
        // TODO: Fill inn implementation code here.
        return null;
    }
}
