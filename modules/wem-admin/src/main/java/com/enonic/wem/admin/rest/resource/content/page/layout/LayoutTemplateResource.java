package com.enonic.wem.admin.rest.resource.content.page.layout;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.admin.rest.resource.content.page.layout.json.ListLayoutTemplateJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.page.layout.LayoutTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;

@Path("content/page/layout/template")
@Produces(MediaType.APPLICATION_JSON)
public class LayoutTemplateResource
    extends AbstractResource
{

    @GET
    @Path( "list" )
    public Result listLayoutTemplates( @QueryParam( "key" ) final String siteTemplateKeyParam )
    {
        try
        {
            SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyParam );

            LayoutTemplates layoutTemplates = client.execute(
                Commands.page().template().layout().getBySiteTemplate().siteTemplate( siteTemplateKey ) );
            return Result.result( new ListLayoutTemplateJson( layoutTemplates ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

}
