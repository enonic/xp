package com.enonic.wem.admin.rest.resource.content.site;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.command.content.site.DeleteSite;
import com.enonic.wem.api.command.content.site.GetNearestSiteByContentId;
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
    public ContentJson create( final CreateSiteJson createSiteJson )
    {
        final CreateSite createSiteCommand = createSiteJson.getCreateSite();
        final Content updatedContent = client.execute( createSiteCommand );

        return new ContentJson( updatedContent );
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson update( final UpdateSiteJson updateSiteJson )
    {
        final UpdateSite updateSiteCommand = updateSiteJson.getUpdateSite();
        final Content updatedContent = client.execute( updateSiteCommand );

        return new ContentJson( updatedContent );
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson delete( final DeleteSiteJson deleteSiteJson )
    {
        final DeleteSite deleteSiteCommand = deleteSiteJson.getDeleteSite();
        final Content deletedContent = client.execute( deleteSiteCommand );

        return new ContentJson( deletedContent );
    }

    @POST
    @Path("nearest")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson getNearest( final GetNearestSiteJson params )
    {
        final GetNearestSiteByContentId command = params.getGetNearestSiteByContentId();
        final Content nearestSite = client.execute( command );
        if ( nearestSite != null )
        {
            return new ContentJson( nearestSite );
        }
        else
        {
            return null;
        }
    }
}
