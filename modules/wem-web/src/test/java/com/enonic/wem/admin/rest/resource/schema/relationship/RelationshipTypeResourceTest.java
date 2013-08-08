package com.enonic.wem.admin.rest.resource.schema.relationship;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Files;
import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.TestUtil;
import com.enonic.wem.admin.rest.resource.schema.relationship.model.AbstractRelationshipTypeJson;
import com.enonic.wem.admin.rest.resource.schema.relationship.model.RelationshipTypeConfigRpcJson;
import com.enonic.wem.admin.rest.resource.schema.relationship.model.RelationshipTypeJson;
import com.enonic.wem.admin.rest.resource.schema.relationship.model.RelationshipTypeListJson;
import com.enonic.wem.admin.rest.resource.schema.relationship.model.RelationshipTypeResultJson;
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
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RelationshipTypeResourceTest
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
        resource = new RelationshipTypeResource();

        client = Mockito.mock( Client.class );
        resource.setClient( client );

        uploadService = Mockito.mock( UploadService.class );
        resource.setUploadService( uploadService );

        mockCurrentContextHttpRequest();
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
    }

    @Test
    public void testRequestGetRelationshipTypeJson_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            module( ModuleName.from( "mymodule" ) ).
            name( "the_relationship_type" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        final QualifiedRelationshipTypeNames names =
            QualifiedRelationshipTypeNames.from( QualifiedRelationshipTypeName.from( "mymodule:the_relationship_type" ) );
        Mockito.when( client.execute( Commands.relationshipType().get().qualifiedNames( names ) ) ).thenReturn( relationshipTypes );

        AbstractRelationshipTypeJson result = resource.get( "mymodule:the_relationship_type", RelationshipTypeResource.FORMAT_JSON );
        assertNotNull( result );

        RelationshipTypeResultJson resultJson = ( ( RelationshipTypeJson ) result ).getRelationshipType();

        assertEquals( "the_relationship_type", resultJson.getName() );
        assertEquals( null, resultJson.getDisplayName() );
        assertEquals( null, resultJson.getFromSemantic() );
        assertEquals( null, resultJson.getToSemantic() );
        assertEquals( 0, resultJson.getAllowedFromTypes().size() );
        assertEquals( 0, resultJson.getAllowedToTypes().size() );
        assertEquals( "http://localhost/admin/rest/schema/image/RelationshipType:mymodule:the_relationship_type", resultJson.getIconUrl() );
    }

    @Test
    public void testRequestGetRelationshipTypeXml_existing()
        throws Exception
    {
        final RelationshipType relationshipType = newRelationshipType().
            module( ModuleName.from( "mymodule" ) ).
            name( "the_relationship_type" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        final QualifiedRelationshipTypeNames names =
            QualifiedRelationshipTypeNames.from( QualifiedRelationshipTypeName.from( "mymodule:the_relationship_type" ) );
        Mockito.when( client.execute( Commands.relationshipType().get().qualifiedNames( names ) ) ).thenReturn( relationshipTypes );

        AbstractRelationshipTypeJson result = resource.get( "mymodule:the_relationship_type", RelationshipTypeResource.FORMAT_XML );
        assertNotNull( result );

        RelationshipTypeConfigRpcJson resultJson = ( RelationshipTypeConfigRpcJson ) result;

        assertEquals( "http://localhost/admin/rest/schema/image/RelationshipType:mymodule:the_relationship_type", resultJson.getIconUrl() );
        assertEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<relationship-type>\r\n  <name>the_relationship_type</name>\r\n  <display-name />\r\n  <module>mymodule</module>\r\n  <from-semantic />\r\n  <to-semantic />\r\n  <allowed-from-types />\r\n  <allowed-to-types />\r\n</relationship-type>\r\n\r\n",
            resultJson.getRelationshipTypeXml() );
    }

    @Test(expected = NotFoundException.class)
    public void testRequestGetRelationshipTypeJson_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetRelationshipTypes.class ) ) ).thenReturn( RelationshipTypes.empty() );

        resource.get( "mymodule:the_relationship_type", RelationshipTypeResource.FORMAT_XML );
    }

    @Test
    public void testList()
        throws Exception
    {
        final RelationshipType relationshipType1 = newRelationshipType().
            module( ModuleName.from( "mymodule" ) ).
            name( "the_relationship_type_1" ).
            build();

        final RelationshipType relationshipType2 = newRelationshipType().
            module( ModuleName.from( "othermodule" ) ).
            name( "the_relationship_type_2" ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType1, relationshipType2 );
        Mockito.when( client.execute( Commands.relationshipType().get().all() ) ).thenReturn( relationshipTypes );

        RelationshipTypeListJson result = resource.list();

        assertNotNull( result );
        assertEquals( 2, result.getTotal() );

        List<String> names = new ArrayList<>( 2 );
        for ( final RelationshipTypeResultJson model : result.getRelationshipTypes() )
        {
            names.add( model.getName() );
        }

        TestUtil.assertUnorderedArraysEquals( new String[]{"the_relationship_type_1", "the_relationship_type_2"}, names.toArray() );
    }

    @Test
    public void deleteSingleRelationshipType()
        throws Exception
    {
        QualifiedRelationshipTypeName.from( "company:partner" );

        Mockito.when( client.execute( Mockito.any( Commands.relationshipType().delete().getClass() ) ) ).thenReturn(
            DeleteRelationshipTypeResult.SUCCESS );

        resource.delete( Arrays.asList( "company:partner" ) );

        Mockito.verify( client, Mockito.times( 1 ) ).execute( Mockito.any( DeleteRelationshipType.class ) );
    }

    @Test(expected = NotFoundException.class)
    public void deleteMultipleRelationshipTypes()
        throws Exception
    {
        QualifiedRelationshipTypeName.from( "company:partner" );

        Mockito.when( client.execute( Mockito.any( Commands.relationshipType().delete().getClass() ) ) ).
            thenReturn( DeleteRelationshipTypeResult.SUCCESS ).
            thenReturn( DeleteRelationshipTypeResult.NOT_FOUND );

        resource.delete( Arrays.asList( "company:partner", "company:client" ) );

        Mockito.verify( client, Mockito.times( 2 ) ).execute( Mockito.any( DeleteRelationshipType.class ) );
    }

    @Test
    public void testCreate()
        throws Exception
    {
        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn( RelationshipTypesExistsResult.empty() );
        Mockito.when( client.execute( isA( CreateRelationshipType.class ) ) ).thenReturn(
            new QualifiedRelationshipTypeName( Module.SYSTEM.getName(), "love" ) );

        String relationshipType = "<relationship-type><name>love</name><display-name>Love</display-name><module>system</module><from-semantic/>loves<to-semantic /><to-semantic/>loved by<to-semantic /><allowed-from-types /><allowed-to-types /></relationship-type>";

        Mockito.when( this.uploadService.getItem( "reference" ) ).thenReturn( null );
        resource.create( relationshipType, "reference" );

        verify( client, times( 1 ) ).execute( isA( CreateRelationshipType.class ) );
        verify( uploadService, times( 1 ) ).getItem( "reference" );
    }

    @Test
    public void testUpdate()
        throws Exception
    {
        QualifiedRelationshipTypeNames qualifiedNames =
            QualifiedRelationshipTypeNames.from( new QualifiedRelationshipTypeName( ModuleName.SYSTEM, "love" ) );

        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn(
            RelationshipTypesExistsResult.from( qualifiedNames ) );
        Mockito.when( client.execute( isA( UpdateRelationshipType.class ) ) ).thenReturn( Boolean.TRUE );

        String relationshipType = "<relationship-type><name>love</name><display-name>Love</display-name><module>system</module><from-semantic/>loves<to-semantic /><to-semantic/>loved by<to-semantic /><allowed-from-types /><allowed-to-types /></relationship-type>";

        Mockito.when( this.uploadService.getItem( "reference" ) ).thenReturn( null );
        resource.update( relationshipType, "reference" );

        verify( client, times( 1 ) ).execute( isA( UpdateRelationshipType.class ) );
        verify( uploadService, times( 1 ) ).getItem( "reference" );
    }

    @Test
    public void testCreateWithIcon()
        throws Exception
    {
        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn( RelationshipTypesExistsResult.empty() );
        Mockito.when( client.execute( isA( CreateRelationshipType.class ) ) ).thenReturn(
            new QualifiedRelationshipTypeName( Module.SYSTEM.getName(), "love" ) );
        uploadFile( "edc1af66-ecb4-4f8a-8df4-0738418f84fc", "icon.png", IMAGE_DATA, "image/png" );

        String relationshipType = "<relationship-type><name>love</name><display-name>Love</display-name><module>system</module><from-semantic/>loves<to-semantic /><to-semantic/>loved by<to-semantic /><allowed-from-types /><allowed-to-types /></relationship-type>";
        String iconReference = "edc1af66-ecb4-4f8a-8df4-0738418f84fc";

        resource.create( relationshipType, iconReference );

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
