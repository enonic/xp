package com.enonic.wem.admin.rest.resource.content.page;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.api.command.content.page.UpdatePage;
import com.enonic.wem.api.content.Content;

@Path("content/page")
@Produces(MediaType.APPLICATION_JSON)
public class PageResource
    extends AbstractResource
{
    @POST
    @Path("create")
    public Result create( final CreatePageJson params )
    {
        client.execute( params.getCreatePage() );

        return null;
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Result update( final UpdatePageJson updatePageJson )
    {
        try
        {
            final UpdatePage command = updatePageJson.getUpdatePage();
            final Content updatedPage = client.execute( command );

            return Result.result( new ContentJson( updatedPage ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }
}
