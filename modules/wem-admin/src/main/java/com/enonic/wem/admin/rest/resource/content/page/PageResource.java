package com.enonic.wem.admin.rest.resource.content.page;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.CreatePage;
import com.enonic.wem.api.content.page.UpdatePage;

@Path("content/page")
@Produces(MediaType.APPLICATION_JSON)
public class PageResource
    extends AbstractResource
{
    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson create( final CreatePageJson params )
    {
        final CreatePage command = params.getCreatePage();
        final Content createdPage = client.execute( command );

        return new ContentJson( createdPage );
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson update( final UpdatePageJson params )
    {
        final UpdatePage command = params.getUpdatePage();
        final Content updatedPage = client.execute( command );

        return new ContentJson( updatedPage );
    }
}
