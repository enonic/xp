package com.enonic.wem.admin.rest.resource.space;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.space.model.SpaceJson;
import com.enonic.wem.admin.rest.resource.space.model.SpaceSummaryListJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.Spaces;

@Path("space")
@Produces(MediaType.APPLICATION_JSON)
public final class SpaceResource
    extends AbstractResource
{
    @GET
    public SpaceJson getDetails( @QueryParam("name") final String name )
    {
        final SpaceName spaceName = SpaceName.from( name );
        final Space space = this.client.execute( Commands.space().get().name( spaceName ) ).first();
        if ( space != null )
        {
            return new SpaceJson( space );
        }
        else
        {
            throw new NotFoundException();
        }
    }

    @GET
    @Path("list")
    public SpaceSummaryListJson list()
    {
        final Spaces spaces = client.execute( Commands.space().get().all() );
        final SpaceSummaryListJson result = new SpaceSummaryListJson( spaces );
        return result;
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete( final List<String> names )
    {
        // Implement. No results need to be returned.
    }
}
