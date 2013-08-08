package com.enonic.wem.admin.rest.resource.schema.relationship;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.relationship.model.AbstractRelationshipTypeJson;
import com.enonic.wem.admin.rest.resource.schema.relationship.model.RelationshipTypeConfigRpcJson;
import com.enonic.wem.admin.rest.resource.schema.relationship.model.RelationshipTypeJson;
import com.enonic.wem.admin.rest.resource.schema.relationship.model.RelationshipTypeListJson;
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
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
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
    public AbstractRelationshipTypeJson get( @QueryParam("qualifiedRelationshipTypeName") final String name, @QueryParam("format") final String format )
    {
        final QualifiedRelationshipTypeNames qualifiedNames = QualifiedRelationshipTypeNames.from( name );
        final GetRelationshipTypes getRelationshipTypes = Commands.relationshipType().get();
        getRelationshipTypes.qualifiedNames( qualifiedNames );

        final RelationshipTypes relationshipTypes = client.execute( getRelationshipTypes );

        if ( !relationshipTypes.isEmpty() )
        {
            RelationshipType type = relationshipTypes.first();

            if ( format.equalsIgnoreCase( FORMAT_JSON ) )
            {
                return new RelationshipTypeJson( type );
            }
            else if ( format.equalsIgnoreCase( FORMAT_XML ) )
            {
                return new RelationshipTypeConfigRpcJson( type );
            }
        }
        throw new NotFoundException();
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
    public void delete( @FormParam("qualifiedRelationshipTypeName") final List<String> names )
    {
        final QualifiedRelationshipTypeNames qualifiedNames = QualifiedRelationshipTypeNames.from(
            names.toArray( new String[names.size()] ) );

        final List<String> failureList = Lists.newArrayList();

        for ( QualifiedRelationshipTypeName relationshipTypeName : qualifiedNames )
        {
            final DeleteRelationshipType deleteCommand = Commands.relationshipType().delete().qualifiedName( relationshipTypeName );
            final DeleteRelationshipTypeResult result = client.execute( deleteCommand );

            if ( result.isNotFound() )
            {
                failureList.add( relationshipTypeName.toString() );
            }
        }

        if ( !failureList.isEmpty() )
        {
            final String nameNotDeleted = Joiner.on( ", " ).join( failureList );
            throw new NotFoundException( String.format( "Relationship Type [%s] was not found", nameNotDeleted ) );
        }
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public void create( @FormParam("relationshipType") final String xml, @FormParam("iconReference") final String iconReference )
    {
        final RelationshipType relationshipType;
        try
        {
            relationshipType = new RelationshipTypeXmlSerializer().toRelationshipType( xml );
        }
        catch ( XmlParsingException e )
        {
            throw new WebApplicationException( e );
        }

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( iconReference );
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
            module( relationshipType.getModuleName() ).
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
    public void update( @FormParam("relationshipType") final String xml, @FormParam("iconReference") final String iconReference )
    {
        final RelationshipType relationshipType;
        try
        {
            relationshipType = new RelationshipTypeXmlSerializer().toRelationshipType( xml );
        }
        catch ( XmlParsingException e )
        {
            throw new WebApplicationException( e );
        }

        final Icon icon;
        try
        {
            icon = new UploadedIconFetcher( uploadService ).getUploadedIcon( iconReference );
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
