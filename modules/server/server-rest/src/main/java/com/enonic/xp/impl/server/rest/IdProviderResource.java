package com.enonic.xp.impl.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
    private final SecurityService securityService;

    @Activate
    public IdProviderResource( @Reference final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @GET
    @Path("list")
    public List<IdProviderJson> list()
    {
        return securityService.getIdProviders().stream().map( IdProviderJson::new ).collect( Collectors.toList() );
    }

}
