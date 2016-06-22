package com.enonic.xp.impl.server.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;

@Path("/api/repo")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true)
public final class ExportResource
    implements JaxRsComponent
{
    // @POST
    // @Path("export")
    // public NodeExportResultJson exportNodes( final ExportNodesRequestJson request )

    // @POST
    // @Path("import")
    // public NodeImportResultJson importNodes( final ImportNodesRequestJson request )
}
