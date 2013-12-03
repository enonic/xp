package com.enonic.wem.admin.rest.resource.content.site;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.command.content.site.UpdateSite;
import com.enonic.wem.api.content.Content;

@Path("content/site")
@Produces(MediaType.APPLICATION_JSON)
public class SiteResource
    extends AbstractResource
{
    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Result create( final CreateSiteJson createSiteJson )
    {
        try
        {
            final CreateSite createSiteCommand = createSiteJson.getCreateSite();
            final Content updatedContent = client.execute( createSiteCommand );

            return Result.result( new ContentJson( updatedContent ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Result update( final UpdateSiteJson updateSiteJson )
    {
        try
        {
            final UpdateSite updateSiteCommand = updateSiteJson.getUpdateSite();
            client.execute( updateSiteCommand );
            final Content updatedContent = updateSiteCommand.getResult();

            return Result.result( new ContentJson( updatedContent ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

}
