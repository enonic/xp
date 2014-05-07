package com.enonic.wem.admin.rest.resource.tools;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.inject.Inject;

import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.initializer.StartupInitializer;

@Path("tools")
public final class ToolsResource
{
    private ElasticsearchIndexService indexService;

    private StartupInitializer startupInitializer;

    @Inject
    public void setIndexService( final ElasticsearchIndexService indexService )
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
        this.indexService.deleteIndex( Index.NODB );
        this.indexService.createIndex( Index.NODB );
        this.indexService.deleteIndex( Index.STORE );
        this.indexService.createIndex( Index.STORE );

        this.startupInitializer.initialize();
        return "Done.";
    }
}
