package com.enonic.wem.admin.rest.resource.schema.relationship;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.UniformInterfaceException;

import junit.framework.Assert;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.CreateRelationshipType;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipType;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.command.schema.relationship.GetAllRelationshipTypes;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipType;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypesExists;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypesExistsResult;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipTypeResult;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNotFoundException;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RelationshipTypeResourceTest
    extends AbstractResourceTest
{
    private RelationshipTypeResource resource;

    private Client client;

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    @Override
    protected Object getResourceInstance()
    {
        resource = new RelationshipTypeResource();

        client = Mockito.mock( Client.class );
        resource.setClient( client );

        return resource;
    }

    @Test
    public void testRequestGetRelationshipTypeJson_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            name( "the_relationship_type" ).
            build();

        final RelationshipTypeName name = RelationshipTypeName.from( "the_relationship_type" );
        Mockito.when( client.execute( Commands.relationshipType().get().byName( name ) ) ).thenReturn( relationshipType );

        String response = resource().path( "schema/relationship" ).queryParam( "name", "the_relationship_type" ).get( String.class );

        assertJson( "get_relationship_type.json", response );

    }

    @Test
    public void testRequestGetRelationshipTypeXml_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            name( "the_relationship_type" ).
            build();

        final RelationshipTypeName name = RelationshipTypeName.from( "the_relationship_type" );
        Mockito.when( client.execute( Commands.relationshipType().get().byName( name ) ) ).thenReturn( relationshipType );

        String response = resource().path( "schema/relationship/config" ).queryParam( "name", "the_relationship_type" ).get( String.class );

        assertJson( "get_relationship_type_config.json", response );
    }

    @Test
    public void testRequestGetRelationshipTypeJson_not_found()
        throws Exception
    {
        try
        {
            Mockito.when( client.execute( Mockito.any( GetRelationshipType.class ) ) ).thenReturn( null );

            resource().path( "schema/relationship" ).queryParam( "name", "relationship_type" ).get( String.class );
        }
        catch ( UniformInterfaceException e )
        {
            Assert.assertEquals( 404, e.getResponse().getStatus() );
            Assert.assertEquals( "RelationshipType [relationship_type] was not found.", e.getResponse().getEntity( String.class ) );
        }
    }

    @Test
    public void testList()
        throws Exception
    {
        final RelationshipType relationshipType1 = newRelationshipType().
            name( "the_relationship_type_1" ).
            build();

        final RelationshipType relationshipType2 = newRelationshipType().
            name( "the_relationship_type_2" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType1, relationshipType2 );
        Mockito.when( client.execute( Mockito.any( GetAllRelationshipTypes.class ) ) ).thenReturn( relationshipTypes );

        String response = resource().path( "schema/relationship/list" ).get( String.class );

        assertJson( "get_relationship_type_list.json", response );
    }

    @Test
    public void deleteSingleRelationshipType()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( Commands.relationshipType().delete().getClass() ) ) ).thenReturn(
            new DeleteRelationshipTypeResult(
                RelationshipType.newRelationshipType().name( RelationshipTypeName.from( "partner" ) ).build() ) );

        String result =
            resource().path( "schema/relationship/delete" ).entity( readFromFile( "delete_single_relationship_type_params.json" ),
                                                                    MediaType.APPLICATION_JSON_TYPE ).post( String.class );

        assertJson( "delete_single_relationship_type.json", result );

        Mockito.verify( client, Mockito.times( 1 ) ).execute( Mockito.any( DeleteRelationshipType.class ) );
    }

    @Test
    public void deleteMultipleRelationshipTypes()
        throws Exception
    {
        RelationshipTypeName partnerRel = RelationshipTypeName.from( "partner" );
        RelationshipTypeName clientRel = RelationshipTypeName.from( "client" );

        Mockito.when( client.execute( Mockito.eq( new DeleteRelationshipType().name( partnerRel ) ) ) ).thenReturn(
            new DeleteRelationshipTypeResult( RelationshipType.newRelationshipType().name( partnerRel ).build() ) );

        Mockito.when( client.execute( Mockito.eq( new DeleteRelationshipType().name( clientRel ) ) ) ).thenThrow(
            new RelationshipTypeNotFoundException( clientRel ) );

        String result =
            resource().path( "schema/relationship/delete" ).entity( readFromFile( "delete_multiple_relationship_type_params.json" ),
                                                                    MediaType.APPLICATION_JSON_TYPE ).post( String.class );

        assertJson( "delete_multiple_relationship_type.json", result );

        Mockito.verify( client, Mockito.times( 2 ) ).execute( Mockito.any( DeleteRelationshipType.class ) );
    }

    @Test
    public void testCreate()
        throws Exception
    {
        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn( RelationshipTypesExistsResult.empty() );
        Mockito.when( client.execute( isA( CreateRelationshipType.class ) ) ).thenReturn( RelationshipTypeName.from( "love" ) );

        resource().path( "schema/relationship/create" ).entity( readFromFile( "create_relationship_type_params.json" ),
                                                                MediaType.APPLICATION_JSON_TYPE ).post();

        verify( client, times( 1 ) ).execute( isA( CreateRelationshipType.class ) );
    }

    @Test
    public void testUpdate()
        throws Exception
    {
        final RelationshipTypeNames relationshipTypeNames = RelationshipTypeNames.from( RelationshipTypeName.from( "love" ) );
        final RelationshipType relationshipType = newRelationshipType().name( "like" ).build();

        final UpdateRelationshipTypeResult result = new UpdateRelationshipTypeResult( relationshipType );

        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn(
            RelationshipTypesExistsResult.from( relationshipTypeNames ) );
        Mockito.when( client.execute( isA( UpdateRelationshipType.class ) ) ).thenReturn( result );

        resource().path( "schema/relationship/update" ).entity( readFromFile( "update_relationship_type_params.json" ),
                                                                MediaType.APPLICATION_JSON_TYPE ).post();

        verify( client, times( 1 ) ).execute( isA( UpdateRelationshipType.class ) );
    }

    @Test
    public void testCreateWithIcon()
        throws Exception
    {
        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn( RelationshipTypesExistsResult.empty() );
        Mockito.when( client.execute( isA( CreateRelationshipType.class ) ) ).thenReturn( RelationshipTypeName.from( "love" ) );

        resource().path( "schema/relationship/create" ).entity( readFromFile( "create_relationship_type_with_icon_params.json" ),
                                                                MediaType.APPLICATION_JSON_TYPE ).post();

        verify( client, times( 1 ) ).execute( isA( CreateRelationshipType.class ) );
    }

}
