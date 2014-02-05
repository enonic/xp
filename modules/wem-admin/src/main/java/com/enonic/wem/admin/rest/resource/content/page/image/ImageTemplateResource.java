package com.enonic.wem.admin.rest.resource.content.page.image;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.image.ImageDescriptorJson;
import com.enonic.wem.admin.json.content.page.image.ImageTemplateJson;
import com.enonic.wem.admin.json.content.page.image.ImageTemplateListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.command.content.page.image.GetImageTemplateByKey;
import com.enonic.wem.api.command.content.page.image.GetImageTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
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
    @Inject
    protected ImageDescriptorService imageDescriptorService;

    @GET
    public ImageTemplateJson getByKey( @QueryParam("siteTemplateKey") final String siteTemplateKeyAsString,
                                       @QueryParam("key") final String imageTemplateKeyAsString )
        throws IOException
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );
        final ImageTemplateKey imageTemplateKey = ImageTemplateKey.from( imageTemplateKeyAsString );
        final GetImageTemplateByKey command = page().template().image().getByKey().
            key( imageTemplateKey ).
            siteTemplateKey( siteTemplateKey );

        final ImageTemplate imageTemplate = client.execute( command );
        final ImageDescriptor descriptor = this.imageDescriptorService.getImageDescriptor( imageTemplate.getDescriptor() );
        return new ImageTemplateJson( imageTemplate, new ImageDescriptorJson( descriptor ) );
    }

    @GET
    @Path("list")
    public ImageTemplateListJson listImageTemplatesBySiteTemplate( @QueryParam("key") final String siteTemplateKeyAsString )
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( siteTemplateKeyAsString );
        final GetImageTemplatesBySiteTemplate listCommand = page().template().image().getBySiteTemplate().
            siteTemplate( siteTemplateKey );
        final ImageTemplates imageTemplates = client.execute( listCommand );
        return new ImageTemplateListJson( imageTemplates );
    }

}
