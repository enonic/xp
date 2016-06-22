package com.enonic.xp.impl.server.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;

@Path("/api/system")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true)
public final class SystemDumpResource
    implements JaxRsComponent
{
    // @POST
    // @Path("dump")
    // public NodeExportResultsJson dump( final SystemDumpRequestJson request )

    // @POST
    // @Path("load")
    // public NodeImportResultsJson load( final SystemLoadRequestJson request )
}
