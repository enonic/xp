package com.enonic.xp.impl.server.rest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.impl.server.rest.model.ApplicationInstallParams;
import com.enonic.xp.impl.server.rest.model.ApplicationInstallResultJson;
import com.enonic.xp.impl.server.rest.model.ApplicationParams;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.multipart.MultipartForm;

@Path("/app")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class ApplicationResource
    implements JaxRsComponent
{
    private final ApplicationResourceService applicationResourceService;

    @Activate
    public ApplicationResource( final @Reference ApplicationResourceService applicationResourceService )
    {
        this.applicationResourceService = applicationResourceService;
    }

    @POST
    @Path("install")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ApplicationInstallResultJson install( final MultipartForm form )
    {
        return applicationResourceService.install( form );
    }

    @POST
    @Path("installUrl")
    @Consumes(MediaType.APPLICATION_JSON)
    public ApplicationInstallResultJson installUrl( final ApplicationInstallParams params )
    {
        return applicationResourceService.installUrl( params );
    }

    @POST
    @Path("uninstall")
    @Consumes(MediaType.APPLICATION_JSON)
    public void uninstall( final ApplicationParams params )
    {
        applicationResourceService.uninstall( params );
    }

    @POST
    @Path("start")
    @Consumes(MediaType.APPLICATION_JSON)
    public void start( final ApplicationParams params )
    {
        applicationResourceService.start( params );
    }

    @POST
    @Path("stop")
    @Consumes(MediaType.APPLICATION_JSON)
    public void stop( final ApplicationParams params )
    {
        applicationResourceService.stop( params );
    }
}
