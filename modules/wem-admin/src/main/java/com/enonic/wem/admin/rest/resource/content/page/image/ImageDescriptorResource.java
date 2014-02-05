package com.enonic.wem.admin.rest.resource.content.page.image;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.image.ImageDescriptorJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;

@Path("content/page/image/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class ImageDescriptorResource
    extends AbstractResource
{
    @Inject
    protected ImageDescriptorService imageDescriptorService;

    @GET
    public ImageDescriptorJson getByKey( @QueryParam("key") final String imageDescriptorKey )
    {
        final ImageDescriptorKey key = ImageDescriptorKey.from( imageDescriptorKey );
        final ImageDescriptor descriptor = imageDescriptorService.getImageDescriptor( key );
        final ImageDescriptorJson json = new ImageDescriptorJson( descriptor );
        return json;
    }

}
