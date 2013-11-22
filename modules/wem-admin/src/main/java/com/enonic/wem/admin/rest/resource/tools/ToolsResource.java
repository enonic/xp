package com.enonic.wem.admin.rest.resource.tools;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.google.inject.Inject;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.initializer.StartupInitializer;

@Path("tools")
public final class ToolsResource
{
    private IndexService indexService;

    private StartupInitializer startupInitializer;

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    @Inject
    public void setStartupInitializer( final StartupInitializer startupInitializer )
    {
        this.startupInitializer = startupInitializer;
    }

    @GET
    @Path("cleanData")
    @Produces("text/plain")
    public String cleanData()
        throws Exception
    {
        this.startupInitializer.initialize( true );
        //this.indexService.reIndex( Index.WEM, Index.NODB );
        return "Done.";
    }

    @GET
    @Path("reIndexData")
    @Produces("text/plain")
    public String reIndexData( @QueryParam("redirect") final String redirect )
        throws Exception
    {
        this.indexService.reIndex( Index.WEM, Index.NODB );
        return "Done.";
    }
}
