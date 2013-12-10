package com.enonic.wem.admin.rest.resource.content.page.layout;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FilenameUtils;

import com.enonic.wem.admin.json.content.page.layout.LayoutDescriptorJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.xml.XmlSerializers;

@Path("content/page/layout/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class LayoutDescriptorResource
    extends AbstractResource
{
    @GET
    public Result getByKey( @QueryParam("key") final String descriptorModuleResourceKey )
    {
        try
        {
            final ModuleResourceKey key = ModuleResourceKey.from( descriptorModuleResourceKey );
            final LayoutDescriptor descriptor = getDescriptor( key, client );
            final LayoutDescriptorJson json = new LayoutDescriptorJson( descriptor );
            return Result.result( json );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    static LayoutDescriptor getDescriptor( final ModuleResourceKey key, final Client client )
        throws IOException
    {
        final GetModuleResource command = Commands.module().getResource().resourceKey( key );
        final Resource descriptorResource = client.execute( command );
        final LayoutDescriptor.Builder builder = LayoutDescriptor.newLayoutDescriptor();
        XmlSerializers.layoutDescriptor().parse( descriptorResource.readAsString() ).to( builder );

        final String descriptorName = FilenameUtils.removeExtension( key.getPath().getName() );
        builder.name( descriptorName );
        return builder.build();
    }
}
