package com.enonic.wem.admin.rest.resource.content.site;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;

@Path("content/site")
@Produces(MediaType.APPLICATION_JSON)
public class SiteResource
    extends AbstractResource
{
    @POST
    @Path("create")
    public CreateSiteResult create( final CreateSiteParams params )
    {
        client.execute( params.getCreateSite() );

        return new CreateSiteResult();
    }

}
