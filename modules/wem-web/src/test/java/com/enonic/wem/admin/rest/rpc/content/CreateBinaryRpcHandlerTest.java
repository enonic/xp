package com.enonic.wem.admin.rest.rpc.content;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Files;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.binary.CreateBinary;
import com.enonic.wem.api.content.binary.BinaryId;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;

public class CreateBinaryRpcHandlerTest
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
        final CreateBinaryRpcHandler handler = new CreateBinaryRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        uploadService = Mockito.mock( UploadService.class );
        handler.setUploadService( uploadService );
        return handler;
    }

    @Test
    public void createBinary()
        throws Exception
    {
        final BinaryId binaryId = BinaryId.from( "f4e4e4ca-fab3-4e15-ad10-ac08aa122f48" );
        Mockito.when( client.execute( isA( CreateBinary.class ) ) ).thenReturn( binaryId );
        final String fileUploadId = "edc1af66-ecb4-4f8a-8df4-0738418f84fc";
        uploadFile( fileUploadId, "photo.png", IMAGE_DATA, "image/png" );

        final ObjectNode params = objectNode();
        params.put( "uploadFileId", fileUploadId );

        final ObjectNode expectedResult = objectNode();
        expectedResult.put( "success", true );
        expectedResult.put( "binaryId", binaryId.toString() );
        testSuccess( params, expectedResult );

        Mockito.verify( client, times( 1 ) ).execute( isA( CreateBinary.class ) );
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
        Mockito.when( this.uploadService.getItem( Mockito.isA( String.class ) ) ).thenReturn( item );
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
