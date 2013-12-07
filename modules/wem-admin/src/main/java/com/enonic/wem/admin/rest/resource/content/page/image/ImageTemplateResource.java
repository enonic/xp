package com.enonic.wem.admin.rest.resource.content.page.image;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.image.ImageDescriptorJson;
import com.enonic.wem.admin.json.content.page.image.ImageTemplateJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.admin.rest.resource.content.page.image.json.ListImageTemplateJson;
import com.enonic.wem.api.command.content.page.image.GetImageTemplateByKey;
import com.enonic.wem.api.command.content.page.image.GetImageTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;
import com.enonic.wem.api.content.page.image.ImageTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;

import static com.enonic.wem.api.command.Commands.page;

@Path("content/page/image/template")
@Produces(MediaType.APPLICATION_JSON)
public class ImageTemplateResource
    extends AbstractResource
{

    @GET
    public Result getByKey( @QueryParam("key") final String imageTemplateKeyAsString )
    {
        try
        {
            final ImageTemplateKey imageTemplateKey = ImageTemplateKey.from( imageTemplateKeyAsString );
            final GetImageTemplateByKey command = page().template().image().getByKey().
                key( imageTemplateKey );

            final ImageTemplate imageTemplate = client.execute( command );
            final ImageDescriptor descriptor = ImageDescriptorResource.getDescriptor( imageTemplate.getDescriptor(), client );
            return Result.result( new ImageTemplateJson( imageTemplate, new ImageDescriptorJson( descriptor ) ) );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    @GET
    @Path("list")
    public Result listImageTemplatesBySiteTemplate( @QueryParam("siteTemplateKey") final String siteTemplateKeyAsString )
    {
        try
        {
            final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );
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
