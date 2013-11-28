package com.enonic.wem.admin.rest.resource.content.site.template;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.content.site.template.json.DeleteSiteTemplateJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.ListSiteTemplateJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.ListSiteTemplateResultJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.DeleteSiteTemplateResultJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
import com.enonic.wem.api.command.content.site.GetAllSiteTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplates;

@Path("content/site/template")
@Produces(MediaType.APPLICATION_JSON)
public class SiteTemplateResource
    extends AbstractResource
{
    @GET
    @Path("list")
    public ListSiteTemplateResultJson listSiteTemplate()
    {
        GetAllSiteTemplates listCommand = Commands.site().template().get().all();
        try
        {
            SiteTemplates siteTemplates = client.execute( listCommand );
            return ListSiteTemplateResultJson.result( new ListSiteTemplateJson( siteTemplates ) );
        }
        catch ( Exception e )
        {
            return ListSiteTemplateResultJson.error( e.getMessage() );
        }
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public DeleteSiteTemplateResultJson deleteSiteTemplate( final DeleteSiteTemplateJson params )
    {
        final DeleteSiteTemplate command = Commands.site().template().delete( params.getKey() );
        try
        {
            final SiteTemplateKey key = client.execute( command );

            return DeleteSiteTemplateResultJson.result( key );
        }
        catch ( Exception e )
        {
            return DeleteSiteTemplateResultJson.error( e.getMessage() );
        }
    }

}
