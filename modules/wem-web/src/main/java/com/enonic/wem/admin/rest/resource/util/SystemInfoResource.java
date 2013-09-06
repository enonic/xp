package com.enonic.wem.admin.rest.resource.util;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.system.SystemInfoJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.Version;

@Path("util/system_info")
@Produces(MediaType.APPLICATION_JSON)
public final class SystemInfoResource
    extends AbstractResource
{
    @GET
    public SystemInfoJson get()
    {
        final SystemInfoJson model = new SystemInfoJson();
        model.setInstallationName( "production" );
        model.setVersion( Version.get().getVersion() );
        model.setTitle( Version.get().getName() );
        return model;
    }
}
