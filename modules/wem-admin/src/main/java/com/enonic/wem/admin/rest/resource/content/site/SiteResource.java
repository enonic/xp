package com.enonic.wem.admin.rest.resource.content.site;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.command.content.site.UpdateSite;
import com.enonic.wem.api.content.site.Site;

@Path("content/site")
@Produces(MediaType.APPLICATION_JSON)
public class SiteResource
    extends AbstractResource
{
    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public CreateSiteResult create( final CreateSiteParams params )
    {
        final CreateSite createSiteCommand = params.getCreateSite();
        client.execute( createSiteCommand );
        final Site createdSite = createSiteCommand.getResult().getSite();

        return CreateSiteResult.success( createdSite );
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public UpdateSiteResult update( final UpdateSiteParams params )
    {
        final UpdateSite updateSiteCommand = params.getUpdateSite();
        client.execute( updateSiteCommand );
        final Site updatedSite = updateSiteCommand.getResult().getSite();
        return UpdateSiteResult.success( updatedSite );
    }

}
