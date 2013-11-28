package com.enonic.wem.admin.rest.resource.content.page;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;

@Path("content/page")
@Produces(MediaType.APPLICATION_JSON)
public class PageResource
    extends AbstractResource
{
    @POST
    @Path("create")
    public CreatePageResult create( final CreatePageJson params )
    {
        client.execute( params.getCreatePage() );

        return null;
    }

}
