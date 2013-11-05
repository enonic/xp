package com.enonic.wem.admin.rest.resource.schema.relationship;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Files;
import com.sun.jersey.api.client.UniformInterfaceException;

import junit.framework.Assert;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.CreateRelationshipType;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipType;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypesExists;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypesExistsResult;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RelationshipTypeResourceTest
    extends AbstractResourceTest
{
    private static byte[] IMAGE_DATA =
        {0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte) 0x80, 0x0, 0x0, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x0, 0x0,
            0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b};

    private RelationshipTypeResource resource;

    private UploadService uploadService;

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

        uploadService = Mockito.mock( UploadService.class );
        resource.setUploadService( uploadService );

        return resource;
    }

    @Test
    public void testRequestGetRelationshipTypeJson_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            name( "the_relationship_type" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        final RelationshipTypeNames names =
            RelationshipTypeNames.from( RelationshipTypeName.from( "the_relationship_type" ) );
        Mockito.when( client.execute( Commands.relationshipType().get().qualifiedNames( names ) ) ).thenReturn( relationshipTypes );

        String response =
            resource().path( "schema/relationship" ).queryParam( "qualifiedName", "the_relationship_type" ).get( String.class );

        assertJson( "get_relationship_type.json", response );

    }

    @Test
    public void testRequestGetRelationshipTypeXml_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            name( "the_relationship_type" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        final RelationshipTypeNames names =
            RelationshipTypeNames.from( RelationshipTypeName.from( "the_relationship_type" ) );
        Mockito.when( client.execute( Commands.relationshipType().get().qualifiedNames( names ) ) ).thenReturn( relationshipTypes );

        String response =
            resource().path( "schema/relationship/config" ).queryParam( "qualifiedName", "the_relationship_type" ).get( String.class );

        assertJson( "get_relationship_type_config.json", response );
    }

    @Test
    public void testRequestGetRelationshipTypeJson_not_found()
        throws Exception
    {
        try
        {
            Mockito.when( client.execute( Mockito.any( GetRelationshipTypes.class ) ) ).thenReturn( RelationshipTypes.empty() );

            resource().path( "schema/relationship" ).queryParam( "qualifiedName", "relationship_type" ).get( String.class );
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
        Mockito.when( client.execute( Commands.relationshipType().get().all() ) ).thenReturn( relationshipTypes );

        String response = resource().path( "schema/relationship/list" ).get( String.class );

        assertJson( "get_relationship_type_list.json", response );
    }

    @Test
    public void deleteSingleRelationshipType()
        throws Exception
    {
        RelationshipTypeName.from( "partner" );

        Mockito.when( client.execute( Mockito.any( Commands.relationshipType().delete().getClass() ) ) ).thenReturn(
            DeleteRelationshipTypeResult.SUCCESS );

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
        RelationshipTypeName.from( "partner" );

        Mockito.when( client.execute( Mockito.any( Commands.relationshipType().delete().getClass() ) ) ).
            thenReturn( DeleteRelationshipTypeResult.SUCCESS ).
            thenReturn( DeleteRelationshipTypeResult.NOT_FOUND );

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

        Mockito.when( this.uploadService.getItem( "reference" ) ).thenReturn( null );

        resource().path( "schema/relationship/create" ).entity( readFromFile( "create_relationship_type_params.json" ),
                                                                MediaType.APPLICATION_JSON_TYPE ).post();

        verify( client, times( 1 ) ).execute( isA( CreateRelationshipType.class ) );
        verify( uploadService, times( 1 ) ).getItem( "reference" );
    }

    @Test
    public void testUpdate()
        throws Exception
    {
        RelationshipTypeNames qualifiedNames = RelationshipTypeNames.from( RelationshipTypeName.from( "love" ) );

        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn(
            RelationshipTypesExistsResult.from( qualifiedNames ) );
        Mockito.when( client.execute( isA( UpdateRelationshipType.class ) ) ).thenReturn( Boolean.TRUE );

        Mockito.when( this.uploadService.getItem( "reference" ) ).thenReturn( null );

        resource().path( "schema/relationship/update" ).entity( readFromFile( "update_relationship_type_params.json" ),
                                                                MediaType.APPLICATION_JSON_TYPE ).post();

        verify( client, times( 1 ) ).execute( isA( UpdateRelationshipType.class ) );
        verify( uploadService, times( 1 ) ).getItem( "reference" );
    }

    @Test
    public void testCreateWithIcon()
        throws Exception
    {
        final String iconReference = "edc1af66-ecb4-4f8a-8df4-0738418f84fc";
        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn( RelationshipTypesExistsResult.empty() );
        Mockito.when( client.execute( isA( CreateRelationshipType.class ) ) ).thenReturn( RelationshipTypeName.from( "love" ) );
        uploadFile( iconReference, "icon.png", IMAGE_DATA, "image/png" );

        resource().path( "schema/relationship/create" ).entity( readFromFile( "create_relationship_type_with_icon_params.json" ),
                                                                MediaType.APPLICATION_JSON_TYPE ).post();

        verify( client, times( 1 ) ).execute( isA( CreateRelationshipType.class ) );
        verify( uploadService, times( 1 ) ).getItem( iconReference );
    }

    private void uploadFile( String id, String name, byte[] data, String type )
        throws Exception
    {
        File file = createTempFile( data );
        UploadItem item = Mockito.mock( UploadItem.class );
        Mockito.when( item.getId() ).thenReturn( id );
        Mockito.when( item.getMimeType() ).thenReturn( type );
        Mockito.when( item.getUploadTime() ).thenReturn( 0L );
        Mockito.when( item.getName() ).thenReturn( name );
        Mockito.when( item.getSize() ).thenReturn( (long) data.length );
        Mockito.when( item.getFile() ).thenReturn( file );
        Mockito.when( this.uploadService.getItem( Mockito.<String>any() ) ).thenReturn( item );
    }

    private File createTempFile( byte[] data )
        throws IOException
    {
        String id = UUID.randomUUID().toString();
        File file = File.createTempFile( id, "" );
        Files.write( data, file );
        return file;
    }
}
