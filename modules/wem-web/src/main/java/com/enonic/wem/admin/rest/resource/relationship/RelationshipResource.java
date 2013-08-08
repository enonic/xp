package com.enonic.wem.admin.rest.resource.relationship;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.relationship.model.CreateRelationshipJson;
import com.enonic.wem.admin.rest.resource.relationship.model.RelationshipCreateParams;
import com.enonic.wem.admin.rest.resource.relationship.model.RelationshipListJson;
import com.enonic.wem.admin.rest.resource.relationship.model.RelationshipUpdateParams;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.relationship.CreateRelationship;
import com.enonic.wem.api.command.relationship.GetRelationships;
import com.enonic.wem.api.command.relationship.UpdateRelationship;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.api.relationship.UpdateRelationshipFailureException;
import com.enonic.wem.api.relationship.editor.RelationshipEditors;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

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

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public CreateRelationshipJson create( final RelationshipCreateParams params )
    {
        final QualifiedRelationshipTypeName typeName = QualifiedRelationshipTypeName.from( params.getType() );
        final ContentId fromContentId = ContentId.from( params.getFromContent() );
        final ContentId toContentId = ContentId.from( params.getToContent() );

        final CreateRelationship createCommand = Commands.relationship().create();
        createCommand.type( typeName ).fromContent( fromContentId ).toContent( toContentId ).property( params.getProperties() );
        client.execute( createCommand );

        return new CreateRelationshipJson(
            RelationshipKey.from( createCommand.getType(), createCommand.getFromContent(), createCommand.getToContent() ) );
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public void update( final RelationshipUpdateParams params )
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

        final UpdateRelationship updateCommand = Commands.relationship().update();
        updateCommand.relationshipKey( params.getRelationshipKey().from() );
        updateCommand.editor( compositeEditorBuilder.build() );

        try
        {
            client.execute( updateCommand );
        }
        catch ( UpdateRelationshipFailureException e )
        {
            throw new WebApplicationException( e );
        }
    }
}
