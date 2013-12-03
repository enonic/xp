package com.enonic.wem.admin.rest.resource.content.page.image;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.admin.rest.resource.content.page.image.json.ListImageTemplateJson;
import com.enonic.wem.api.command.content.page.image.GetImageTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;

import static com.enonic.wem.api.command.Commands.page;

@Path("content/page/image/template")
@Produces(MediaType.APPLICATION_JSON)
public class ImageTemplateResource
    extends AbstractResource
{

    @GET
    @Path("list")
    public Result listImageTemplatesBySiteTemplate( @QueryParam("key") final String siteTemplateKeyParam )
    {
        try
        {
            final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyParam );
            final GetImageTemplatesBySiteTemplate listCommand = page().template().image().getBySiteTemplate().
                siteTemplate( siteTemplateKey );
            final ImageTemplates imageTemplates = client.execute( listCommand );
            return Result.result( new ListImageTemplateJson( imageTemplates ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

}
