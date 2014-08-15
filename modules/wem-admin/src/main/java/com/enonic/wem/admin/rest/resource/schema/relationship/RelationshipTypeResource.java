package com.enonic.wem.admin.rest.resource.schema.relationship;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.json.icon.ThumbnailJson;
import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeConfigJson;
import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeJson;
import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeListJson;
import com.enonic.wem.admin.rest.resource.schema.json.CreateOrUpdateSchemaJsonResult;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteParams;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.schema.relationship.CreateRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNotFoundException;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.api.schema.relationship.UpdateRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.editor.RelationshipTypeEditor;
import com.enonic.wem.api.xml.XmlSerializers;

@Path("schema/relationship")
@Produces(MediaType.APPLICATION_JSON)
public class RelationshipTypeResource
{
    private RelationshipTypeService relationshipTypeService;

    private BlobService blobService;

    @GET
    public RelationshipTypeJson get( @QueryParam("name") final String name )
    {
        final RelationshipTypeName relationshipTypeName = RelationshipTypeName.from( name );
        final RelationshipType relationshipType = fetchRelationshipType( relationshipTypeName );

        if ( relationshipType == null )
        {
            String message = String.format( "RelationshipType [%s] was not found.", relationshipTypeName );
            throw new WebApplicationException( Response.status( Response.Status.NOT_FOUND ).
                entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        return new RelationshipTypeJson( relationshipType );
    }

    @GET
    @Path("config")
    public RelationshipTypeConfigJson getConfig( @QueryParam("name") final String name )
    {
        final RelationshipTypeName relationshipTypeName = RelationshipTypeName.from( name );
        final RelationshipType relationshipType = fetchRelationshipType( relationshipTypeName );

        if ( relationshipType == null )
        {
            String message = String.format( "RelationshipType [%s] was not found.", relationshipTypeName );
            throw new WebApplicationException( Response.status( Response.Status.NOT_FOUND ).
                entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        return new RelationshipTypeConfigJson( relationshipType );

    }

    public RelationshipType fetchRelationshipType( final RelationshipTypeName name )
    {
        final GetRelationshipTypeParams params = new GetRelationshipTypeParams().name( name );
        return relationshipTypeService.getByName( params );
    }

    @GET
    @Path("list")
    public RelationshipTypeListJson list()
    {
        final RelationshipTypes relationshipTypes = relationshipTypeService.getAll();

        return new RelationshipTypeListJson( relationshipTypes );
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public SchemaDeleteJson delete( SchemaDeleteParams params )
    {
        final RelationshipTypeNames relationshipTypeNames =
            RelationshipTypeNames.from( params.getNames().toArray( new String[params.getNames().size()] ) );

        final SchemaDeleteJson deletionResult = new SchemaDeleteJson();
        for ( RelationshipTypeName relationshipTypeName : relationshipTypeNames )
        {
            try
            {
                relationshipTypeService.delete( relationshipTypeName );
                deletionResult.success( relationshipTypeName );
            }
            catch ( RelationshipTypeNotFoundException e )
            {
                deletionResult.failure( relationshipTypeName, e.getMessage() );
            }
        }

        return deletionResult;
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public CreateOrUpdateSchemaJsonResult create( RelationshipTypeCreateJson json )
    {
        try
        {
            final RelationshipType.Builder builder = RelationshipType.newRelationshipType().name( json.getName().toString() );
            XmlSerializers.relationshipType().parse( json.getConfig() ).to( builder );
            final RelationshipType relationshipType = builder.build();

            final Icon schemaIcon = getSchemaIcon( json.getThumbnailJson() );

            final CreateRelationshipTypeParams createCommand = new CreateRelationshipTypeParams().
                name( json.getName() ).
                displayName( relationshipType.getDisplayName() ).
                description( relationshipType.getDescription() ).
                fromSemantic( relationshipType.getFromSemantic() ).
                toSemantic( relationshipType.getToSemantic() ).
                allowedFromTypes( relationshipType.getAllowedFromTypes() ).
                allowedToTypes( relationshipType.getAllowedToTypes() ).
                schemaIcon( schemaIcon );

            this.relationshipTypeService.create( createCommand );

            return CreateOrUpdateSchemaJsonResult.result( new RelationshipTypeJson( relationshipType ) );
        }
        catch ( Exception e )
        {
            return CreateOrUpdateSchemaJsonResult.error( e.getMessage() );
        }
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public CreateOrUpdateSchemaJsonResult update( final RelationshipTypeUpdateJson json )
    {
        try
        {
            final RelationshipType.Builder builder = RelationshipType.newRelationshipType().name( json.getName().toString() );
            XmlSerializers.relationshipType().parse( json.getConfig() ).to( builder );
            final RelationshipType parsed = builder.build();

            final Icon schemaIcon = getSchemaIcon( json.getThumbnailJson() );

            final RelationshipTypeEditor editor = new RelationshipTypeEditor()
            {
                @Override
                public RelationshipType edit( final RelationshipType relationshipType )
                {
                    final RelationshipType.Builder builder = RelationshipType.newRelationshipType( relationshipType );
                    builder.name( json.getName() );
                    builder.displayName( parsed.getDisplayName() );
                    builder.description( parsed.getDescription() );
                    builder.fromSemantic( parsed.getFromSemantic() );
                    builder.toSemantic( parsed.getToSemantic() );
                    builder.addAllowedFromTypes( parsed.getAllowedFromTypes() );
                    builder.addAllowedToTypes( parsed.getAllowedToTypes() );
                    if ( schemaIcon != null )
                    {
                        builder.icon( schemaIcon );
                    }
                    return builder.build();
                }
            };

            final UpdateRelationshipTypeParams updateCommand = new UpdateRelationshipTypeParams().
                name( json.getRelationshipTypeToUpdate() ).
                editor( editor );

            relationshipTypeService.update( updateCommand );

            return CreateOrUpdateSchemaJsonResult.result( new RelationshipTypeJson( parsed ) );
        }
        catch ( Exception e )
        {
            return CreateOrUpdateSchemaJsonResult.error( e.getMessage() );
        }
    }

    private Icon getSchemaIcon( final ThumbnailJson thumbnailJson )
    {
        if ( thumbnailJson != null )
        {
            final Blob blob = blobService.get( thumbnailJson.getThumbnail().getBlobKey() );
            return blob == null ? null : Icon.from( blob.getStream(), thumbnailJson.getMimeType() );
        }
        return null;
    }

    @Inject
    public void setRelationshipTypeService( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    @Inject
    public void setBlobService( final BlobService blobService )
    {
        this.blobService = blobService;
    }
}
