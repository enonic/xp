package com.enonic.wem.admin.rest.resource.tools;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.inject.Inject;

import com.enonic.wem.core.initializer.StartupInitializer;

@Path("tools")
public final class ToolsResource
{
    @Inject
    protected StartupInitializer startupInitializer;

    @GET
    @Path("cleanData")
    @Produces("text/plain")
    public String cleanData()
        throws Exception
    {
        this.startupInitializer.initialize( true );
        return "Done.";
    }
}
