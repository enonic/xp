package com.enonic.wem.web.rest.rpc.content.schema.mixin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Files;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.schema.mixin.CreateMixin;
import com.enonic.wem.api.command.content.schema.mixin.GetMixins;
import com.enonic.wem.api.command.content.schema.mixin.UpdateMixins;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.Mixins;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.web.rest.service.upload.UploadItem;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static com.enonic.wem.api.content.schema.mixin.Mixin.newMixin;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CreateOrUpdateMixinRpcHandlerTest
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
        final CreateOrUpdateMixinRpcHandler handler = new CreateOrUpdateMixinRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        uploadService = Mockito.mock( UploadService.class );
        handler.setUploadService( uploadService );
        return handler;
    }

    @Test
    public void testCreateMixin()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateMixin_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( CreateMixin.class ) );
    }

    @Test
    public void testUpdateMixin()
        throws Exception
    {
        final Input input = newInput().name( "someInput" ).inputType( InputTypes.TEXT_LINE ).build();
        final Mixin existingMixin = newMixin().formItem( input ).module( Module.SYSTEM.getName() ).build();
        final Mixins mixins = Mixins.from( existingMixin );
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( mixins );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", false );
        resultJson.put( "updated", true );
        testSuccess( "createOrUpdateMixin_param.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( UpdateMixins.class ) );
    }

    @Test
    public void testCreateMixinWithIcon()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.empty() );
        uploadFile( "edc1af66-ecb4-4f8a-8df4-0738418f84fc", "icon.png", IMAGE_DATA, "image/png" );

        final ObjectNode resultJson = objectNode();
        resultJson.put( "success", true );
        resultJson.put( "created", true );
        resultJson.put( "updated", false );
        testSuccess( "createOrUpdateMixin_param_with_icon.json", resultJson );

        verify( client, times( 1 ) ).execute( isA( CreateMixin.class ) );
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