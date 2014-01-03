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

import static com.enonic.wem.api.command.Commands.page;

@Path("content/page/part/descriptor")
@Produces(MediaType.APPLICATION_JSON)
public class PartDescriptorResource
    extends AbstractResource
{
    @GET
    public PartDescriptorJson getByKey( @QueryParam("key") final String partDescriptorKey )
    {
        final PartDescriptorKey key = PartDescriptorKey.from( partDescriptorKey );
        final PartDescriptor descriptor = getDescriptor( key, client );
        final PartDescriptorJson json = new PartDescriptorJson( descriptor );
        return json;
    }

    private PartDescriptor getDescriptor( final PartDescriptorKey key, final Client client )
    {
        final GetPartDescriptor getPartDescriptor = page().descriptor().part().getByKey( key );
        return client.execute( getPartDescriptor );
    }
}
