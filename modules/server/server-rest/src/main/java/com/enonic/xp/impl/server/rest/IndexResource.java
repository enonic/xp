package com.enonic.xp.impl.server.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;

@Path("/api/repo/index")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true)
public final class IndexResource
    implements JaxRsComponent
{
    // @POST
    // @Path("reindex")
    // public ReindexResultJson reindex( final ReindexRequestJson request )

    // @POST
    // @Path("updateSettings")
    // public UpdateIndexSettingsResultJson updateSettings( final UpdateIndexSettingsRequestJson request )
}
