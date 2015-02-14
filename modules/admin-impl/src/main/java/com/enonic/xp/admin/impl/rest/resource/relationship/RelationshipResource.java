package com.enonic.xp.admin.impl.rest.resource.relationship;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.AdminResource;
import com.enonic.xp.admin.impl.rest.exception.NotFoundWebException;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.relationship.json.CreateRelationshipJson;
import com.enonic.xp.admin.impl.rest.resource.relationship.json.RelationshipCreateParams;
import com.enonic.xp.admin.impl.rest.resource.relationship.json.RelationshipListJson;
import com.enonic.xp.admin.impl.rest.resource.relationship.json.RelationshipUpdateParams;
import com.enonic.xp.admin.impl.rest.resource.relationship.json.UpdateRelationshipJson;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.CreateRelationshipParams;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.relationship.RelationshipService;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.api.relationship.UpdateRelationshipFailureException;
import com.enonic.wem.api.relationship.UpdateRelationshipParams;
import com.enonic.wem.api.relationship.editor.RelationshipEditors;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "relationship")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class RelationshipResource
    implements AdminResource
{
    private RelationshipService relationshipService;

    @GET
    public RelationshipListJson get( @QueryParam("fromContent") final String fromContent )
    {
        final Relationships relationships = this.relationshipService.getAll( ContentId.from( fromContent ) );
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

        final CreateRelationshipParams createCommand = new CreateRelationshipParams().type( typeName ).
            fromContent( fromContentId ).toContent( toContentId ).property( params.getProperties() );

        this.relationshipService.create( createCommand );

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
            final UpdateRelationshipParams updateCommand =
                new UpdateRelationshipParams().relationshipKey( params.getRelationshipKey().from() );
            updateCommand.editor( compositeEditorBuilder.build() );

            this.relationshipService.update( updateCommand );
            return new UpdateRelationshipJson( params.getRelationshipKey().from() );
        }
        catch ( final UpdateRelationshipFailureException e )
        {
            throw new NotFoundWebException( e.getMessage() );
        }
    }

    @Reference
    public void setRelationshipService( final RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }
}
