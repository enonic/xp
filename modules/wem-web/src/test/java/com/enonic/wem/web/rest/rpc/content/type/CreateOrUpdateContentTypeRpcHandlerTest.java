package com.enonic.wem.web.rest.rpc.content.type;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Files;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.type.CreateContentType;
import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.command.content.type.UpdateContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.web.rest.service.upload.UploadItem;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CreateOrUpdateContentTypeRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private static final byte[] SINGLE_PIXEL_GIF_PICTURE =
        {0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte) 0x80, 0x0, 0x0, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x0, 0x0,
            0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b};

    private Client client;

    private UploadService uploadService;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final CreateOrUpdateContentTypeRpcHandler handler = new CreateOrUpdateContentTypeRpcHandler();
        client = Mockito.mock( Client.class );
        uploadService = Mockito.mock( UploadService.class );
        handler.setClient( client );
        handler.setUploadService( uploadService );

        return handler;
    }

    @Test
    public void testCreateContentType()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateContentType_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( CreateContentType.class ) );
    }

    @Test
    public void testUpdateContentType()
        throws Exception
    {
        final ContentType existingContentType = ContentType.newContentType().name( "aType" ).module( Module.SYSTEM.getName() ).build();
        final ContentTypes contentTypes = ContentTypes.from( existingContentType );
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( contentTypes );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateContentType_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( UpdateContentTypes.class ) );
    }

    @Test
    public void testCreateContentTypeWithIcon()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.empty() );
        uploadFile( "edc1af66-ecb4-4f8a-8df4-0738418f84fc", "photo.png", SINGLE_PIXEL_GIF_PICTURE, "image/png" );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateContentType_param_with_icon.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( CreateContentType.class ) );
    }

    private void uploadFile( final String id, final String name, final byte[] data, final String type )
        throws Exception
    {
        final File file = createTempFile( data );
        final UploadItem item = Mockito.mock( UploadItem.class );
        Mockito.when( item.getId() ).thenReturn( id );
        Mockito.when( item.getMimeType() ).thenReturn( type );
        Mockito.when( item.getUploadTime() ).thenReturn( 0L );
        Mockito.when( item.getName() ).thenReturn( name );
        Mockito.when( item.getSize() ).thenReturn( (long) data.length );
        Mockito.when( item.getFile() ).thenReturn( file );
        Mockito.when( this.uploadService.getItem( Mockito.<String>any() ) ).thenReturn( item );
    }

    private File createTempFile( final byte[] data )
        throws IOException
    {
        final String id = UUID.randomUUID().toString();
        final File file = File.createTempFile( id, "" );
        Files.write( data, file );
        return file;
    }
}