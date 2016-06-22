package com.enonic.xp.impl.server.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;

@Path("/api/repo/snapshot")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true)
public final class SnapshotResource
    implements JaxRsComponent
{
    // @POST
    // public SnapshotResultJson snapshot( final SnapshotRequestJson params )

    // @POST
    // @Path("restore")
    // public RestoreResultJson restore( final RestoreRequestJson params )

    // @POST
    // @Path("delete")
    // public DeleteSnapshotsResultJson delete( final DeleteSnapshotRequestJson params )

    // @GET
    // @Path("list")
    // public SnapshotResultsJson list()
}
