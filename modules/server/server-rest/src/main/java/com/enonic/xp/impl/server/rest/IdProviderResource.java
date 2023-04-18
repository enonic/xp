package com.enonic.xp.impl.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.impl.server.rest.model.IdProviderJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;

@Path("/idproviders")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class IdProviderResource
    implements JaxRsComponent
{
    private SecurityService securityService;

    @GET
    @Path("list")
    public List<IdProviderJson> list()
    {
        return securityService.getIdProviders().stream().map( IdProviderJson::new ).collect( Collectors.toList() );
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

}
