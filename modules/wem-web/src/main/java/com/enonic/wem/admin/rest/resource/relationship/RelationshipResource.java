package com.enonic.wem.admin.rest.resource.relationship;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.relationship.model.RelationshipListJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.relationship.GetRelationships;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.Relationships;

@Path("relationship")
@Produces(MediaType.APPLICATION_JSON)
public class RelationshipResource
    extends AbstractResource
{
    @GET
    public RelationshipListJson get( @QueryParam("fromContent") final String fromContent )
    {
        final ContentId contentId = ContentId.from( fromContent );
        final GetRelationships command = Commands.relationship().get().fromContent( contentId );
        final Relationships relationships = client.execute( command );
        final RelationshipListJson jsonResult = new RelationshipListJson( relationships );

        return jsonResult;
    }

    // create() @POST
    // update() @POST
}
