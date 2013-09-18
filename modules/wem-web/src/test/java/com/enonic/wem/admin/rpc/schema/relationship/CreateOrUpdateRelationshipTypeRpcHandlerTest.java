package com.enonic.wem.admin.rpc.schema.relationship;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Files;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.relationship.CreateRelationshipType;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypesExists;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypesExistsResult;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeNames;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CreateOrUpdateRelationshipTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private static byte[] IMAGE_DATA =
        {0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte) 0x80, 0x0, 0x0, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x0, 0x0,
            0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b};

    private Client client;

    private UploadService uploadService;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        CreateOrUpdateRelationshipTypeRpcHandler handler = new CreateOrUpdateRelationshipTypeRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        uploadService = Mockito.mock( UploadService.class );
        handler.setUploadService( uploadService );
        return handler;
    }

    @Test
    public void create()
        throws Exception
    {
        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn( RelationshipTypesExistsResult.empty() );
        Mockito.when( client.execute( isA( CreateRelationshipType.class ) ) ).thenReturn(
            new QualifiedRelationshipTypeName( Module.SYSTEM.getName(), "love" ) );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateRelationshipType_create_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( CreateRelationshipType.class ) );
    }

    @Test
    public void update()
        throws Exception
    {

        QualifiedRelationshipTypeNames qualifiedNames =
            QualifiedRelationshipTypeNames.from( new QualifiedRelationshipTypeName( ModuleName.SYSTEM, "love" ) );

        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn(
            RelationshipTypesExistsResult.from( qualifiedNames ) );
        Mockito.when( client.execute( isA( UpdateRelationshipType.class ) ) ).thenReturn( Boolean.TRUE );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateRelationshipType_update_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( UpdateRelationshipType.class ) );
    }

    @Test
    public void createWithIcon()
        throws Exception
    {
        Mockito.when( client.execute( isA( RelationshipTypesExists.class ) ) ).thenReturn( RelationshipTypesExistsResult.empty() );
        Mockito.when( client.execute( isA( CreateRelationshipType.class ) ) ).thenReturn(
            new QualifiedRelationshipTypeName( Module.SYSTEM.getName(), "love" ) );
        uploadFile( "edc1af66-ecb4-4f8a-8df4-0738418f84fc", "icon.png", IMAGE_DATA, "image/png" );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateRelationshipType_with_icon_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( CreateRelationshipType.class ) );
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