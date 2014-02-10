package com.enonic.wem.admin.rest.resource.content.page.image;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.image.ImageDescriptorJson;
import com.enonic.wem.admin.json.content.page.image.ImageDescriptorsJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorService;
import com.enonic.wem.api.content.page.image.ImageDescriptors;

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
        return new ImageDescriptorJson( descriptor );
    }

    @POST
    @Path("list/by_modules")
    @Consumes(MediaType.APPLICATION_JSON)
    public ImageDescriptorsJson getByModules( final GetByModulesParams params )
    {
        final ImageDescriptors descriptors = imageDescriptorService.getImageDescriptorsByModules( params.getModuleKeys() );
        return new ImageDescriptorsJson( descriptors );
    }

}
