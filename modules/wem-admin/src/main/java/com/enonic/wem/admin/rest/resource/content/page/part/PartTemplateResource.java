package com.enonic.wem.admin.rest.resource.content.page.part;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.admin.rest.resource.content.page.part.json.PartTemplateListJson;
import com.enonic.wem.admin.rest.resource.content.page.part.json.PartTemplateListParamsJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.page.part.GetPartTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.part.PartTemplates;

@Path("content/page/part/template")
@Produces(MediaType.APPLICATION_JSON)
public class PartTemplateResource
    extends AbstractResource
{

    @Path( "list" )
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Result list(PartTemplateListParamsJson params) {
        try {

            GetPartTemplatesBySiteTemplate command = Commands.page().template().part().getBySiteTemplate().siteTemplate( params.getKey() );
            PartTemplates partTemplates = this.client.execute( command );

            return Result.result( new PartTemplateListJson( partTemplates ) );

        } catch(Exception e) {
            return Result.exception( e );
        }
    }
}

