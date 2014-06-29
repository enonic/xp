package com.enonic.wem.admin.rest.resource.schema.relationship;

import java.io.ByteArrayInputStream;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.admin.rest.resource.MockRestResponse;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.schema.relationship.CreateRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNotFoundException;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.api.schema.relationship.RelationshipTypesExistsResult;
import com.enonic.wem.api.schema.relationship.UpdateRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.UpdateRelationshipTypeResult;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RelationshipTypeResourceTest
    extends AbstractResourceTest
{
    private RelationshipTypeService relationshipTypeService;

    private BlobService blobService;

    @Override
    protected Object getResourceInstance()
    {
        final RelationshipTypeResource resource = new RelationshipTypeResource();

        relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
        resource.setRelationshipTypeService( relationshipTypeService );

        blobService = Mockito.mock( BlobService.class );
        resource.setBlobService( blobService );

        return resource;
    }

    @Test
    public void testRequestGetRelationshipTypeJson_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            name( "the_relationship_type" ).
            description( "RT description" ).
            build();

        final RelationshipTypeName name = RelationshipTypeName.from( "the_relationship_type" );
        final GetRelationshipTypeParams params = new GetRelationshipTypeParams().name( name );
        Mockito.when( relationshipTypeService.getByName( params ) ).thenReturn( relationshipType );

        String response = request().path( "schema/relationship" ).queryParam( "name", "the_relationship_type" ).get().getAsString();

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
        final GetRelationshipTypeParams params = new GetRelationshipTypeParams().name( name );
        Mockito.when( relationshipTypeService.getByName( params ) ).thenReturn( relationshipType );

        String response = request().path( "schema/relationship/config" ).queryParam( "name", "the_relationship_type" ).get().getAsString();

        assertJson( "get_relationship_type_config.json", response );
    }

    @Test
    public void testRequestGetRelationshipTypeJson_not_found()
        throws Exception
    {
        Mockito.when( relationshipTypeService.getByName( Mockito.any( GetRelationshipTypeParams.class ) ) ).thenReturn( null );

        final MockRestResponse response = request().path( "schema/relationship" ).queryParam( "name", "relationship_type" ).get();
        Assert.assertEquals( 404, response.getStatus() );
        Assert.assertEquals( "RelationshipType [relationship_type] was not found.", response.getAsString() );
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
        Mockito.when( relationshipTypeService.getAll() ).thenReturn( relationshipTypes );

        String response = request().path( "schema/relationship/list" ).get().getAsString();

        assertJson( "get_relationship_type_list.json", response );
    }

    @Test
    public void deleteSingleRelationshipType()
        throws Exception
    {
        Mockito.when( relationshipTypeService.delete( Mockito.any( RelationshipTypeName.class ) ) ).thenReturn(
            new DeleteRelationshipTypeResult(
                RelationshipType.newRelationshipType().name( RelationshipTypeName.from( "partner" ) ).build() ) );

        String result =
            request().path( "schema/relationship/delete" ).entity( readFromFile( "delete_single_relationship_type_params.json" ),
                                                                   MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        assertJson( "delete_single_relationship_type.json", result );

        Mockito.verify( relationshipTypeService, Mockito.times( 1 ) ).delete( Mockito.any( RelationshipTypeName.class ) );
    }

    @Test
    public void deleteMultipleRelationshipTypes()
        throws Exception
    {
        RelationshipTypeName partnerRel = RelationshipTypeName.from( "partner" );
        RelationshipTypeName clientRel = RelationshipTypeName.from( "client" );

        Mockito.when( relationshipTypeService.delete( Mockito.eq( partnerRel ) ) ).thenReturn(
            new DeleteRelationshipTypeResult( RelationshipType.newRelationshipType().name( partnerRel ).build() ) );

        Mockito.when( relationshipTypeService.delete( Mockito.eq( clientRel ) ) ).thenThrow(
            new RelationshipTypeNotFoundException( clientRel ) );

        String result =
            request().path( "schema/relationship/delete" ).entity( readFromFile( "delete_multiple_relationship_type_params.json" ),
                                                                   MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        assertJson( "delete_multiple_relationship_type.json", result );

        Mockito.verify( relationshipTypeService, Mockito.times( 2 ) ).delete( Mockito.any( RelationshipTypeName.class ) );
    }

    @Test
    public void testCreate()
        throws Exception
    {
        Mockito.when( relationshipTypeService.exists( isA( RelationshipTypeNames.class ) ) ).thenReturn(
            RelationshipTypesExistsResult.empty() );
        Mockito.when( relationshipTypeService.create( isA( CreateRelationshipTypeParams.class ) ) ).thenReturn(
            RelationshipTypeName.from( "love" ) );

        request().path( "schema/relationship/create" ).entity( readFromFile( "create_relationship_type_params.json" ),
                                                               MediaType.APPLICATION_JSON_TYPE ).post();

        verify( relationshipTypeService, times( 1 ) ).create( isA( CreateRelationshipTypeParams.class ) );
    }

    @Test
    public void testUpdate()
        throws Exception
    {
        final RelationshipTypeNames relationshipTypeNames = RelationshipTypeNames.from( RelationshipTypeName.from( "love" ) );
        final RelationshipType relationshipType = newRelationshipType().name( "like" ).build();

        final UpdateRelationshipTypeResult result = new UpdateRelationshipTypeResult( relationshipType );

        Mockito.when( relationshipTypeService.exists( isA( RelationshipTypeNames.class ) ) ).thenReturn(
            RelationshipTypesExistsResult.from( relationshipTypeNames ) );
        Mockito.when( relationshipTypeService.update( isA( UpdateRelationshipTypeParams.class ) ) ).thenReturn( result );

        request().path( "schema/relationship/update" ).entity( readFromFile( "update_relationship_type_params.json" ),
                                                               MediaType.APPLICATION_JSON_TYPE ).post();

        verify( relationshipTypeService, times( 1 ) ).update( isA( UpdateRelationshipTypeParams.class ) );
    }

    @Test
    public void testCreateWithIcon()
        throws Exception
    {
        Mockito.when( relationshipTypeService.exists( isA( RelationshipTypeNames.class ) ) ).thenReturn(
            RelationshipTypesExistsResult.empty() );
        Mockito.when( relationshipTypeService.create( isA( CreateRelationshipTypeParams.class ) ) ).thenReturn(
            RelationshipTypeName.from( "love" ) );
        final Blob iconBlob = Mockito.mock( Blob.class );
        Mockito.when( iconBlob.getStream() ).thenReturn( new ByteArrayInputStream( "icondata".getBytes() ) );
        Mockito.when( blobService.get( isA( BlobKey.class ) ) ).thenReturn( iconBlob );

        request().path( "schema/relationship/create" ).entity( readFromFile( "create_relationship_type_with_icon_params.json" ),
                                                               MediaType.APPLICATION_JSON_TYPE ).post();

        verify( relationshipTypeService, times( 1 ) ).create( isA( CreateRelationshipTypeParams.class ) );
    }

}
