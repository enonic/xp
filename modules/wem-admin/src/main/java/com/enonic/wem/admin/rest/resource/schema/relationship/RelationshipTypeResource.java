package com.enonic.wem.admin.rest.resource.schema.relationship;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeJson;
import com.enonic.wem.admin.json.schema.relationship.RelationshipTypeListJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconResolver;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconUrlResolver;
import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

@Path("schema/relationship")
@Produces(MediaType.APPLICATION_JSON)
public class RelationshipTypeResource
{
    private RelationshipTypeService relationshipTypeService;

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

        return new RelationshipTypeJson( relationshipType, newSchemaIconUrlResolver() );
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

        return new RelationshipTypeListJson( relationshipTypes, newSchemaIconUrlResolver() );
    }

    private SchemaIconUrlResolver newSchemaIconUrlResolver()
    {
        return new SchemaIconUrlResolver( new SchemaIconResolver( relationshipTypeService ) );
    }

    public void setRelationshipTypeService( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }
}
