package com.enonic.wem.admin.rest.resource.content.page.part;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FilenameUtils;

import com.enonic.wem.admin.json.content.page.part.PartDescriptorJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.Result;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.xml.XmlSerializers;

@Path("content/page/part/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class PartDescriptorResource
    extends AbstractResource
{
    @GET
    public Result getByKey( @QueryParam("key") final String descriptorModuleResourceKey )
    {
        try
        {
            final ModuleResourceKey key = ModuleResourceKey.from( descriptorModuleResourceKey );
            final PartDescriptor descriptor = getDescriptor( key, client );
            final PartDescriptorJson json = new PartDescriptorJson( descriptor );
            return Result.result( json );
        }
        catch ( Exception e )
        {
            return Result.exception( e );
        }
    }

    static PartDescriptor getDescriptor( final ModuleResourceKey key, final Client client )
        throws IOException
    {
        final GetModuleResource command = Commands.module().getResource().resourceKey( key );
        final Resource descriptorResource = client.execute( command );
        final PartDescriptor.Builder builder = PartDescriptor.newPartDescriptor();
        XmlSerializers.partDescriptor().parse( descriptorResource.readAsString() ).to( builder );

        final String descriptorName = FilenameUtils.removeExtension( key.getPath().getName() );
        builder.name( descriptorName );
        return builder.build();
    }
}
