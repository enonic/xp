package com.enonic.wem.admin.rest.resource.security;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.UserStores;


@SuppressWarnings("UnusedDeclaration")
@Path("userstore")
@Produces(MediaType.APPLICATION_JSON)
public class SecurityResource
{

    private SecurityService securityService;

    @GET
    public UserStores getUserStores()
    {
        return securityService.getUserStores();
    }


    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public SecurityService getIdentityService()
    {
        return securityService;
    }

}
