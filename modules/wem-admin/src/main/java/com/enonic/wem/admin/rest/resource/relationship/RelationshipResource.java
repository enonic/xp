package com.enonic.wem.admin.rest.resource.relationship;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.relationship.json.CreateRelationshipJson;
import com.enonic.wem.admin.rest.resource.relationship.json.RelationshipCreateParams;
import com.enonic.wem.admin.rest.resource.relationship.json.RelationshipListJson;
import com.enonic.wem.admin.rest.resource.relationship.json.RelationshipUpdateParams;
import com.enonic.wem.admin.rest.resource.relationship.json.UpdateRelationshipJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.relationship.CreateRelationship;
import com.enonic.wem.api.command.relationship.GetRelationships;
import com.enonic.wem.api.command.relationship.UpdateRelationship;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.api.relationship.UpdateRelationshipFailureException;
import com.enonic.wem.api.relationship.editor.RelationshipEditors;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

@Path("relationship")
@Produces(MediaType.APPLICATION_JSON)
public class RelationshipResource
    extends AbstractResource
{
    @GET
    public RelationshipListJson get( @QueryParam("fromContent") final String fromContent )
    {
        final GetRelationships command = Commands.relationship().get().fromContent( ContentId.from( fromContent ) );

        final Relationships relationships = client.execute( command );

        return new RelationshipListJson( relationships );
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public CreateRelationshipJson create( final RelationshipCreateParams params )
    {
        final RelationshipTypeName typeName = RelationshipTypeName.from( params.getType() );
        final ContentId fromContentId = ContentId.from( params.getFromContent() );
        final ContentId toContentId = ContentId.from( params.getToContent() );

        final CreateRelationship createCommand = Commands.relationship().create().type( typeName ).
            fromContent( fromContentId ).toContent( toContentId ).property( params.getProperties() );

        RelationshipId relationshipId = client.execute( createCommand );

        return new CreateRelationshipJson(
            RelationshipKey.from( createCommand.getType(), createCommand.getFromContent(), createCommand.getToContent() ) );
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public UpdateRelationshipJson update( final RelationshipUpdateParams params )
    {
        final RelationshipEditors.CompositeBuilder compositeEditorBuilder = RelationshipEditors.newCompositeBuilder();
        if ( params.getAdd() != null )
        {
            compositeEditorBuilder.add( RelationshipEditors.addProperties( params.getAdd() ) );
        }
        if ( params.getRemove() != null && !params.getRemove().isEmpty() )
        {
            compositeEditorBuilder.add( RelationshipEditors.removeProperties( params.getRemove() ) );
        }

        try
        {
            final UpdateRelationship updateCommand = Commands.relationship().update().relationshipKey( params.getRelationshipKey().from() );
            updateCommand.editor( compositeEditorBuilder.build() );

            client.execute( updateCommand );
            return new UpdateRelationshipJson( params.getRelationshipKey().from() );
        }
        catch ( final UpdateRelationshipFailureException e )
        {
            throw new NotFoundException( e.getMessage() );
        }
    }
}
