package com.enonic.xp.impl.server.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;

@Path("/api/app")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true)
public final class ApplicationResource
    implements JaxRsComponent
{
    // @POST
    // @Path("install")
    // @Consumes(MediaType.MULTIPART_FORM_DATA)
    // public ApplicationInstallResultJson install( final MultipartForm form )

    // @POST
    // @Path("installUrl")
    // @Consumes(MediaType.APPLICATION_JSON)
    // public ApplicationInstallResultJson installUrl( final ApplicationInstallParams params )
}
