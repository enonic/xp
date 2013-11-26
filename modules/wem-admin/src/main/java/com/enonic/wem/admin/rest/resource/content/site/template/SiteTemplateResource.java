package com.enonic.wem.admin.rest.resource.content.site.template;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.content.site.template.json.DeleteSiteTemplateJson;
import com.enonic.wem.admin.rest.resource.content.site.template.json.DeleteSiteTemplateParams;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;

@Path("content/site/template")
@Produces(MediaType.APPLICATION_JSON)
public class SiteTemplateResource
    extends AbstractResource
{


    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public DeleteSiteTemplateJson deleteSiteTemplate( final DeleteSiteTemplateParams params )
    {
        final DeleteSiteTemplate command = Commands.site().template().delete( params.getKey() );
        try
        {
            final SiteTemplateKey key = client.execute( command );

            return DeleteSiteTemplateJson.result( key );
        }
        catch ( Exception e )
        {
            return DeleteSiteTemplateJson.error( e.getMessage() );
        }
    }

}
