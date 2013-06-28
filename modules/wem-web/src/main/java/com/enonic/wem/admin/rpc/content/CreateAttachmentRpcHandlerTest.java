package com.enonic.wem.admin.rpc.content;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Files;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.attachment.CreateAttachment;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class CreateAttachmentRpcHandlerTest
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
        final CreateAttachmentRpcHandler handler = new CreateAttachmentRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        uploadService = Mockito.mock( UploadService.class );
        handler.setUploadService( uploadService );
        return handler;
    }

    @Test
    public void createAttachmentByContentPath()
        throws Exception
    {
        final String fileUploadId = "edc1af66-ecb4-4f8a-8df4-0738418f84fc";
        uploadFile( fileUploadId, "photo.png", IMAGE_DATA, "image/png" );

        final ObjectNode params = objectNode();
        params.put( "contentPath", "myspace:/parent/child" );
        params.put( "uploadFileId", fileUploadId );

        testSuccess( params, "createAttachment_contentPath_result.json" );

        Mockito.verify( client, times( 1 ) ).execute( isA( CreateAttachment.class ) );
    }

    @Test
    public void createAttachmentByContentId()
        throws Exception
    {
        final String fileUploadId = "edc1af66-ecb4-4f8a-8df4-0738418f84fc";
        uploadFile( fileUploadId, "photo.png", IMAGE_DATA, "image/png" );

        final ObjectNode params = objectNode();
        params.put( "contentId", "7fb16ce6-8c30-4309-bfa1-6ae5ebe19e2c" );
        params.put( "uploadFileId", fileUploadId );

        testSuccess( params, "createAttachment_contentId_result.json" );

        Mockito.verify( client, times( 1 ) ).execute( isA( CreateAttachment.class ) );
    }

    @Test
    public void createAttachmentMissingFile()
        throws Exception
    {
        final String fileUploadId = "edc1af66-ecb4-4f8a-8df4-0738418f84fc";
        uploadFile( fileUploadId, "photo.png", IMAGE_DATA, "image/png" );

        final ObjectNode params = objectNode();
        params.put( "contentPath", "myspace:/parent/child" );
        params.put( "uploadFileId", "62c8e12e-4e2a-46d4-aa57-d433801250ff" );

        final ObjectNode expectedResult = objectNode();
        expectedResult.put( "success", false );
        expectedResult.put( "error", "Could not find uploaded files with id: 62c8e12e-4e2a-46d4-aa57-d433801250ff" );

        testSuccess( params, expectedResult );

        Mockito.verify( client, never( ) ).execute( isA( CreateAttachment.class ) );
    }

    @Test
    public void createAttachmentMissingContentSelectorParam()
        throws Exception
    {
        final ObjectNode params = objectNode();
        testError( params, "Invalid params: Parameter [contentId] or [contentPath] must be specified" );

        Mockito.verify( client, never() ).execute( isA( CreateAttachment.class ) );
    }

    @Test
    public void createAttachmentMissingUploadFileParam()
        throws Exception
    {
        final ObjectNode params = objectNode();
        params.put( "contentPath", "myspace:/parent/child" );
        testError( params, "Invalid params: Parameter [uploadFileId] is required" );

        Mockito.verify( client, never() ).execute( isA( CreateAttachment.class ) );
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
        Mockito.when( this.uploadService.getItem( eq( id ) ) ).thenReturn( item );
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
