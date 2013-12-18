package com.enonic.wem.admin.rest.resource.content.page.part;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.json.content.page.part.PartDescriptorJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.part.GetPartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.module.ModuleResourceKey;

import static com.enonic.wem.api.command.Commands.page;

@Path("content/page/part/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class PartDescriptorResource
    extends AbstractResource
{
    @GET
    public PartDescriptorJson getByKey( @QueryParam("key") final String descriptorModuleResourceKey )
    {
        final ModuleResourceKey key = ModuleResourceKey.from( descriptorModuleResourceKey );
        final PartDescriptor descriptor = getDescriptor( key, client );
        final PartDescriptorJson json = new PartDescriptorJson( descriptor );
        return json;
    }

    private PartDescriptor getDescriptor( final ModuleResourceKey key, final Client client )
    {
        final PartDescriptorKey partDescriptorKey = PartDescriptorKey.from( key.getModuleKey(), key.getPath() );
        final GetPartDescriptor getPartDescriptor = page().descriptor().part().getByKey( partDescriptorKey );
        return client.execute( getPartDescriptor );
    }
}
