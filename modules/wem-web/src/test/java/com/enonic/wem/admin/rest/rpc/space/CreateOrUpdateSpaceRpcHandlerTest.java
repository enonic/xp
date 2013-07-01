package com.enonic.wem.admin.rest.rpc.space;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Files;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.space.CreateSpace;
import com.enonic.wem.api.command.space.GetSpaces;
import com.enonic.wem.api.command.space.UpdateSpace;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.Spaces;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;

public class CreateOrUpdateSpaceRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    private UploadService uploadService;

    private static final DateTime CURRENT_TIME = new DateTime( 2000, 1, 1, 12, 0, 0 );

    private static byte[] IMAGE_DATA =
        {0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte) 0x80, 0x0, 0x0, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x0, 0x0,
            0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b};

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final CreateOrUpdateSpaceRpcHandler handler = new CreateOrUpdateSpaceRpcHandler();

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
        Mockito.when( client.execute( isA( GetSpaces.class ) ) ).thenReturn( Spaces.empty() );

        final ObjectNode params = objectNode();
        params.put( "spaceName", "mySpace" );
        params.put( "displayName", "My spacey" );

        final ObjectNode expectedResult = objectNode();
        expectedResult.put( "success", true );
        expectedResult.put( "created", true );
        expectedResult.put( "updated", false );
        testSuccess( params, expectedResult );

        Mockito.verify( client, times( 1 ) ).execute( isA( CreateSpace.class ) );
    }

    @Test
    public void update()
        throws Exception
    {
        final Space space = Space.newSpace().
            name( SpaceName.from( "mySpace" ) ).
            displayName( "My Space" ).
            modifiedTime( CURRENT_TIME ).
            createdTime( CURRENT_TIME ).
            rootContent( ContentId.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            build();

        Mockito.when( client.execute( isA( GetSpaces.class ) ) ).thenReturn( Spaces.from( space ) );
        uploadFile( "edc1af66-ecb4-4f8a-8df4-0738418f84fc", "photo.png", IMAGE_DATA, "image/png" );

        final ObjectNode params = objectNode();
        params.put( "spaceName", "mySpace" );
        params.put( "displayName", "My spacey" );
        params.put( "iconReference", "ABCDEF" );

        final ObjectNode expectedResult = objectNode();
        expectedResult.put( "success", true );
        expectedResult.put( "created", false );
        expectedResult.put( "updated", true );
        testSuccess( params, expectedResult );

        Mockito.verify( client, times( 1 ) ).execute( isA( UpdateSpace.class ) );
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
