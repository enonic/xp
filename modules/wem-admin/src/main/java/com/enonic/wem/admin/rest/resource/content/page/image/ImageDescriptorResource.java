package com.enonic.wem.admin.rest.resource.content.page.image;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.image.ImageDescriptorJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.image.GetImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.module.ModuleResourceKey;

import static com.enonic.wem.api.command.Commands.page;

@Path("content/page/image/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class ImageDescriptorResource
    extends AbstractResource
{
    @GET
    public ImageDescriptorJson getByKey( @QueryParam("key") final String descriptorModuleResourceKey )
    {
        final ModuleResourceKey key = ModuleResourceKey.from( descriptorModuleResourceKey );
        final ImageDescriptor descriptor = getDescriptor( key, client );
        final ImageDescriptorJson json = new ImageDescriptorJson( descriptor );
        return json;
    }

    private ImageDescriptor getDescriptor( final ModuleResourceKey key, final Client client )
    {
        final ImageDescriptorKey imageDescriptorKey = ImageDescriptorKey.from( key.getModuleKey(), key.getPath() );
        final GetImageDescriptor getImageDescriptor = page().descriptor().image().getByKey( imageDescriptorKey );
        return client.execute( getImageDescriptor );
    }
}
