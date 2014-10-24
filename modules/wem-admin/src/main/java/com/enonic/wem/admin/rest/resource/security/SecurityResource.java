package com.enonic.wem.admin.rest.resource.security;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.security.json.UserStoreJson;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStores;


@SuppressWarnings("UnusedDeclaration")
@Path("userstore")
@Produces(MediaType.APPLICATION_JSON)
public class SecurityResource
{

    private SecurityService securityService;

    @GET
    @Path("list")
    public List<UserStoreJson> getUserStores()
    {
        UserStores userStores = securityService.getUserStores();
        List<UserStoreJson> userStoresJson = new ArrayList<>();
        List<UserStore> list = userStores.getList();
        for ( UserStore userStore : list )
        {
            userStoresJson.add( new UserStoreJson( userStore ) );
        }
        return userStoresJson;
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

}
