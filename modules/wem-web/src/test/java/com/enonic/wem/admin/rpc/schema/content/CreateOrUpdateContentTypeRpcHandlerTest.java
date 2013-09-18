package com.enonic.wem.admin.rpc.schema.content;

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
import com.enonic.wem.api.command.schema.content.CreateContentType;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.schema.content.UpdateContentType;
import com.enonic.wem.api.command.schema.content.UpdateContentTypeResult;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CreateOrUpdateContentTypeRpcHandlerTest
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
        CreateOrUpdateContentTypeRpcHandler handler = new CreateOrUpdateContentTypeRpcHandler();
        client = Mockito.mock( Client.class );
        handler.setClient( client );

        uploadService = Mockito.mock( UploadService.class );
        handler.setUploadService( uploadService );
        return handler;
    }

    @Test
    public void create_ContentType()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateContentType_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( CreateContentType.class ) );
    }

    @Test
    public void update_ContentType()
        throws Exception
    {
        ContentType existingContentType = ContentType.newContentType().name( "a_type" ).module( Module.SYSTEM.getName() ).build();
        ContentTypes contentTypes = ContentTypes.from( existingContentType );
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( contentTypes );
        Mockito.when( client.execute( isA( UpdateContentType.class ) ) ).thenReturn( UpdateContentTypeResult.SUCCESS );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateContentType_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( UpdateContentType.class ) );
    }

    @Test
    public void update_ContentType_with_failure()
        throws Exception
    {
        ContentType existingContentType = ContentType.newContentType().name( "a_type" ).module( Module.SYSTEM.getName() ).build();
        ContentTypes contentTypes = ContentTypes.from( existingContentType );
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( contentTypes );
        Mockito.when( client.execute( isA( UpdateContentType.class ) ) ).thenReturn( UpdateContentTypeResult.NOT_FOUND );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", false );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        resultJson.put( "failure", "NOT_FOUND" );
        testSuccess( "createOrUpdateContentType_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( UpdateContentType.class ) );
    }

    @Test
    public void create_ContentType_with_Icon()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );
        uploadFile( "edc1af66-ecb4-4f8a-8df4-0738418f84fc", "photo.png", IMAGE_DATA, "image/png" );

        ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateContentType_param_with_icon.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( CreateContentType.class ) );
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