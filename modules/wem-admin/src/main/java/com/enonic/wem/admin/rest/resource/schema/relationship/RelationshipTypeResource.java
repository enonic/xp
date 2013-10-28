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

import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeConfigJson;
import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeJson;
import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeListJson;
import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.json.SchemaDeleteParams;
import com.enonic.wem.admin.rest.resource.schema.relationship.json.RelationshipTypeCreateOrUpdateParam;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rpc.UploadedIconFetcher;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.CreateRelationshipType;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipType;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.api.schema.relationship.editor.SetRelationshipTypeEditor;
import com.enonic.wem.core.schema.relationship.RelationshipTypeXmlSerializer;
import com.enonic.wem.core.support.serializer.XmlParsingException;

@Path("schema/relationship")
@Produces(MediaType.APPLICATION_JSON)
public class RelationshipTypeResource
    extends AbstractResource
{
    public static final String FORMAT_XML = "XML";

    public static final String FORMAT_JSON = "JSON";

    private UploadService uploadService;

    @GET
    public RelationshipTypeJson get( @QueryParam("qualifiedName") final String name )
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
    public RelationshipTypeConfigJson getConfig( @QueryParam("qualifiedName") final String name )
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
        final GetRelationshipTypes command = Commands.relationshipType().get().qualifiedNames( RelationshipTypeNames.from( name ) );
        final RelationshipTypes relationshipTypes = client.execute( command );
        return relationshipTypes.isEmpty() ? null : relationshipTypes.first();
    }

    @GET
    @Path("list")
    public RelationshipTypeListJson list()
    {
        final RelationshipTypes relationshipTypes = client.execute( Commands.relationshipType().get().all() );

        return new RelationshipTypeListJson( relationshipTypes );
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public SchemaDeleteJson delete( SchemaDeleteParams param )
    {
        final RelationshipTypeNames qualifiedNames =
            RelationshipTypeNames.from( param.getQualifiedNames().toArray( new String[param.getQualifiedNames().size()] ) );

        final SchemaDeleteJson deletionResult = new SchemaDeleteJson();
        for ( RelationshipTypeName relationshipTypeName : qualifiedNames )
        {
            final DeleteRelationshipType deleteCommand = Commands.relationshipType().delete().qualifiedName( relationshipTypeName );
            final DeleteRelationshipTypeResult result = client.execute( deleteCommand );

            switch ( result )
            {
                case SUCCESS:
                    deletionResult.success( relationshipTypeName );
                    break;

                case NOT_FOUND:
                    deletionResult.failure( relationshipTypeName,
                                            String.format( "Mixin [%s] was not found", relationshipTypeName.toString() ) );
                    break;

            }
        }

        return deletionResult;
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public void create( RelationshipTypeCreateOrUpdateParam param )
    {
        final RelationshipType relationshipType;
        try
        {
            relationshipType = new RelationshipTypeXmlSerializer().toRelationshipType( param.getRelationshipType() );
        }
        catch ( XmlParsingException e )
        {
            throw new WebApplicationException( e );
        }

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( param.getIconReference() );
        }
        catch ( Exception e )
        {
            throw new WebApplicationException( e );
        }

        createRelationshipType( relationshipType, icon );
    }

    private void createRelationshipType( final RelationshipType relationshipType, final Icon icon )
    {
        final CreateRelationshipType createCommand = Commands.relationshipType().create();
        createCommand.
            name( relationshipType.getName() ).
            displayName( relationshipType.getDisplayName() ).
            fromSemantic( relationshipType.getFromSemantic() ).
            toSemantic( relationshipType.getToSemantic() ).
            allowedFromTypes( relationshipType.getAllowedFromTypes() ).
            allowedToTypes( relationshipType.getAllowedToTypes() ).
            icon( icon );

        try
        {
            client.execute( createCommand );
        }
        catch ( BaseException e )
        {
            throw new WebApplicationException( e );
        }
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public void update( RelationshipTypeCreateOrUpdateParam param )
    {
        final RelationshipType relationshipType;
        try
        {
            relationshipType = new RelationshipTypeXmlSerializer().toRelationshipType( param.getRelationshipType() );
        }
        catch ( XmlParsingException e )
        {
            throw new WebApplicationException( e );
        }

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( param.getIconReference() );
        }
        catch ( Exception e )
        {
            throw new WebApplicationException( e );
        }

        updateRelationshipType( relationshipType, icon );
    }

    private void updateRelationshipType( final RelationshipType relationshipType, final Icon icon )
    {
        final UpdateRelationshipType updateCommand = Commands.relationshipType().update();
        updateCommand.selector( relationshipType.getQualifiedName() );
        updateCommand.editor( SetRelationshipTypeEditor.newSetRelationshipTypeEditor().
            displayName( relationshipType.getDisplayName() ).
            fromSemantic( relationshipType.getFromSemantic() ).
            toSemantic( relationshipType.getToSemantic() ).
            allowedFromTypes( relationshipType.getAllowedFromTypes() ).
            allowedToTypes( relationshipType.getAllowedToTypes() ).
            icon( icon ).
            build() );

        try
        {
            client.execute( updateCommand );
        }
        catch ( BaseException e )
        {
            throw new WebApplicationException( e );
        }
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
