package com.enonic.wem.admin.rest.resource.content.page.part;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.admin.rest.resource.content.page.part.json.PartTemplateListJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.part.GetPartTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.part.PartTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;

@Path("content/page/part/template")
@Produces(MediaType.APPLICATION_JSON)
public class PartTemplateResource
    extends AbstractResource
{

    @Path("list")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Result list( @QueryParam("siteTemplateKey") String siteTemplateKeyAsString )
    {
        try
        {

            GetPartTemplatesBySiteTemplate command =
                Commands.page().template().part().getBySiteTemplate().siteTemplate( SiteTemplateKey.from( siteTemplateKeyAsString ) );
            PartTemplates partTemplates = this.client.execute( command );

            return Result.result( new PartTemplateListJson( partTemplates ) );

        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }
}

