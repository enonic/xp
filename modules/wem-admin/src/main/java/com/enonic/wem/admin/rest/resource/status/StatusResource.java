package com.enonic.wem.admin.rest.resource.status;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.Version;
import com.enonic.xp.web.jaxrs.JaxRsComponent;

@Path(ResourceConstants.REST_ROOT + "status")
public final class StatusResource
    implements JaxRsComponent
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StatusResult getStatus()
    {
        final StatusResult result = new StatusResult();
        result.setVersion( Version.get().getVersion() );
        result.setInstallation( "production" );
        return result;
    }
}
