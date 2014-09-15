package com.enonic.wem.admin.rest.resource.schema.relationship;

import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.admin.rest.resource.MockRestResponse;
import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;

public class RelationshipTypeResourceTest
    extends AbstractResourceTest
{
    private RelationshipTypeService relationshipTypeService;

    @Override
    protected Object getResourceInstance()
    {
        final RelationshipTypeResource resource = new RelationshipTypeResource();

        relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
        resource.setRelationshipTypeService( relationshipTypeService );

        return resource;
    }

    @Test
    public void testRequestGetRelationshipTypeJson_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            name( "mymodule-1.0.0:the_relationship_type" ).
            description( "RT description" ).
            build();

        final RelationshipTypeName name = RelationshipTypeName.from( "mymodule-1.0.0:the_relationship_type" );
        final GetRelationshipTypeParams params = new GetRelationshipTypeParams().name( name );
        Mockito.when( relationshipTypeService.getByName( params ) ).thenReturn( relationshipType );

        String response =
            request().path( "schema/relationship" ).queryParam( "name", "mymodule-1.0.0:the_relationship_type" ).get().getAsString();

        assertJson( "get_relationship_type.json", response );

    }

    @Test
    public void testRequestGetRelationshipTypeXml_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            name( "mymodule-1.0.0:the_relationship_type" ).
            build();

        final RelationshipTypeName name = RelationshipTypeName.from( "mymodule-1.0.0:the_relationship_type" );
        final GetRelationshipTypeParams params = new GetRelationshipTypeParams().name( name );
        Mockito.when( relationshipTypeService.getByName( params ) ).thenReturn( relationshipType );

        String response =
            request().path( "schema/relationship/config" ).queryParam( "name", "mymodule-1.0.0:the_relationship_type" ).get().getAsString();

        assertJson( "get_relationship_type_config.json", response );
    }

    @Test
    public void testRequestGetRelationshipTypeJson_not_found()
        throws Exception
    {
        Mockito.when( relationshipTypeService.getByName( Mockito.any( GetRelationshipTypeParams.class ) ) ).thenReturn( null );

        final MockRestResponse response =
            request().path( "schema/relationship" ).queryParam( "name", "mymodule-1.0.0:relationship_type" ).get();
        Assert.assertEquals( 404, response.getStatus() );
        Assert.assertEquals( "RelationshipType [mymodule-1.0.0:relationship_type] was not found.", response.getAsString() );
    }

    @Test
    public void testList()
        throws Exception
    {
        final RelationshipType relationshipType1 = newRelationshipType().
            name( "mymodule-1.0.0:the_relationship_type_1" ).
            build();

        final RelationshipType relationshipType2 = newRelationshipType().
            name( "mymodule-1.0.0:the_relationship_type_2" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType1, relationshipType2 );
        Mockito.when( relationshipTypeService.getAll() ).thenReturn( relationshipTypes );

        String response = request().path( "schema/relationship/list" ).get().getAsString();

        assertJson( "get_relationship_type_list.json", response );
    }
}
