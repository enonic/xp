package com.enonic.wem.admin.rest.resource.schema.mixin;

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
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;

import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_LINE;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MixinResourceTest
    extends AbstractResourceTest
{
    private static QualifiedMixinName MY_MIXIN_QUALIFIED_NAME_1 = new QualifiedMixinName( "mymodule:input_text_1" );

    private static QualifiedMixinName MY_MIXIN_QUALIFIED_NAME_2 = new QualifiedMixinName( "othermodule:text_area_1" );

    private static byte[] IMAGE_DATA =
        {0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte) 0x80, 0x0, 0x0, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x0, 0x0,
            0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b};

    private MixinResource resource = new MixinResource();

    private Client client;

    private UploadService uploadService;

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        resource = new MixinResource();
        resource.setClient( client );

        uploadService = Mockito.mock( UploadService.class );
        resource.setUploadService( uploadService );

        return resource;
    }

    @Test
    public final void test_get_mixin()
        throws Exception
    {
        Mixin mixin =
            Mixin.newMixin().name( MY_MIXIN_QUALIFIED_NAME_1.getMixinName() ).module( MY_MIXIN_QUALIFIED_NAME_1.getModuleName() ).formItem(
                newInput().name( MY_MIXIN_QUALIFIED_NAME_1.getLocalName() ).inputType( TEXT_LINE ).label( "Line Text 1" ).required(
                    true ).helpText( "Help text line 1" ).required( true ).build() ).build();

        Mockito.when( client.execute( Mockito.isA( GetMixins.class ) ) ).thenReturn( Mixins.from( mixin ) );

        String response =
            resource().path( "schema/mixin" ).queryParam( "qualifiedName", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get( String.class );

        assertJson( "get_mixin.json", response );
    }

    @Test
    public final void test_get_mixin_config()
        throws Exception
    {
        Mixin mixin =
            Mixin.newMixin().name( MY_MIXIN_QUALIFIED_NAME_1.getMixinName() ).module( MY_MIXIN_QUALIFIED_NAME_1.getModuleName() ).formItem(
                newInput().name( MY_MIXIN_QUALIFIED_NAME_1.getLocalName() ).inputType( TEXT_LINE ).label( "Line Text 1" ).required(
                    true ).helpText( "Help text line 1" ).required( true ).build() ).build();

        Mockito.when( client.execute( Mockito.isA( GetMixins.class ) ) ).thenReturn( Mixins.from( mixin ) );

        String result = resource().path( "schema/mixin/config" ).queryParam( "qualifiedName", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get(
            String.class );

        assertJson( "get_mixin_config.json", result );
    }

    @Test
    public final void test_get_mixin_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetMixins.class ) ) ).thenReturn( Mixins.empty() );
        try
        {
            String result =
                resource().path( "schema/mixin" ).queryParam( "qualifiedName", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get( String.class );

            Assert.assertFalse( "Exception should've been thrown already", true );
        }
        catch ( UniformInterfaceException e )
        {
            Assert.assertEquals( 404, e.getResponse().getStatus() );
            Assert.assertEquals( "Mixin [mymodule:input_text_1] was not found.", e.getResponse().getEntity( String.class ) );
        }
    }

    @Test
    public final void test_get_mixin_config_not_found()
    {
        Mockito.when( client.execute( Mockito.any( GetMixins.class ) ) ).thenReturn( Mixins.empty() );
        try
        {
            String result =
                resource().path( "schema/mixin/config" ).queryParam( "qualifiedName", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get(
                    String.class );

            Assert.assertFalse( "Exception should've been thrown already", true );
        }
        catch ( UniformInterfaceException e )
        {
            Assert.assertEquals( 404, e.getResponse().getStatus() );
            Assert.assertEquals( "Mixin [mymodule:input_text_1] was not found.", e.getResponse().getEntity( String.class ) );
        }
    }

    @Test
    public final void test_list_mixins()
        throws Exception
    {
        Mixin mixin1 =
            Mixin.newMixin().name( MY_MIXIN_QUALIFIED_NAME_1.getMixinName() ).module( MY_MIXIN_QUALIFIED_NAME_1.getModuleName() ).formItem(
                newInput().name( MY_MIXIN_QUALIFIED_NAME_1.getLocalName() ).inputType( TEXT_LINE ).label( "Line Text 1" ).required(
                    true ).helpText( "Help text line 1" ).required( true ).build() ).build();

        Mixin mixin2 =
            Mixin.newMixin().name( MY_MIXIN_QUALIFIED_NAME_2.getMixinName() ).module( MY_MIXIN_QUALIFIED_NAME_2.getModuleName() ).formItem(
                newInput().name( MY_MIXIN_QUALIFIED_NAME_2.getLocalName() ).inputType( TEXT_AREA ).label( "Text Area" ).required(
                    true ).helpText( "Help text area" ).required( true ).build() ).build();

        Mockito.when( client.execute( Mockito.isA( GetMixins.class ) ) ).thenReturn( Mixins.from( mixin1, mixin2 ) );

        String result = resource().path( "schema/mixin/list" ).get( String.class );

        assertJson( "list_mixins.json", result );
    }

    @Test
    public void test_create_mixin()
        throws Exception
    {
        // setup
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        String result = resource().path( "schema/mixin/create" ).entity( readFromFile( "create_mixin_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "create_mixin.json", result );
        verify( client, times( 1 ) ).execute( isA( CreateMixin.class ) );
    }

    @Test
    public void test_create_mixin_already_exists()
        throws Exception
    {
        Mixin mixin = newMixin().name( "some_input" ).module( Module.SYSTEM.getName() ).formItem(
            newInput().name( "some_input" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.from( mixin ) );

        try
        {
            String result = resource().path( "schema/mixin/create" ).entity( readFromFile( "create_mixin_params.json" ),
                                                                             MediaType.APPLICATION_JSON_TYPE ).post( String.class );
            Assert.assertFalse( "Exception should've been thrown already", true );
        }
        catch ( UniformInterfaceException e )
        {
            Assert.assertEquals( 409, e.getResponse().getStatus() );
            Assert.assertEquals( "Mixin [mymodule:my_set] already exists.", e.getResponse().getEntity( String.class ) );
            verify( client, never() ).execute( isA( CreateMixin.class ) );
        }
    }

    @Test
    public void test_create_mixin_with_icon()
        throws Exception
    {
        // setup
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        uploadFile( "edc1af66-ecb4-4f8a-8df4-0738418f84fc", "icon.png", IMAGE_DATA, "image/png" );

        String result = resource().path( "schema/mixin/create" ).entity( readFromFile( "create_mixin_with_icon_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );

        assertJson( "create_mixin.json", result );
        verify( uploadService, times( 1 ) ).getItem( "edc1af66-ecb4-4f8a-8df4-0738418f84fc" );
        verify( client, times( 1 ) ).execute( isA( CreateMixin.class ) );
    }

    @Test
    public void test_update_mixin()
        throws Exception
    {
        Mixin mixin = newMixin().name( "some_input" ).module( Module.SYSTEM.getName() ).formItem(
            newInput().name( "some_input" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.from( mixin ) );

        uploadFile( "edc1af66-ecb4-4f8a-8df4-0738418f84fc", "icon.png", IMAGE_DATA, "image/png" );

        String result = resource().path( "schema/mixin/update" ).entity( readFromFile( "create_mixin_with_icon_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "update_mixin.json", result );
        verify( uploadService, times( 1 ) ).getItem( "edc1af66-ecb4-4f8a-8df4-0738418f84fc" );
        verify( client, times( 1 ) ).execute( isA( UpdateMixin.class ) );
    }

    @Test
    public void test_update_mixin_not_found()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.empty() );
        try
        {
            String result = resource().path( "schema/mixin/update" ).entity( readFromFile( "create_mixin_params.json" ),
                                                                             MediaType.APPLICATION_JSON_TYPE ).post( String.class );
            Assert.assertFalse( "Exception should've been thrown already", true );
        }
        catch ( UniformInterfaceException e )
        {
            Assert.assertEquals( 404, e.getResponse().getStatus() );
            Assert.assertEquals( "Mixin [mymodule:my_set] not found.", e.getResponse().getEntity( String.class ) );
            verify( client, never() ).execute( isA( UpdateMixin.class ) );
        }
    }

    @Test
    public void test_delete_single_mixin()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( DeleteMixin.class ) ) ).thenReturn( DeleteMixinResult.SUCCESS );

        String result = resource().path( "schema/mixin/delete" ).entity( readFromFile( "delete_single_mixin_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "delete_single_mixin.json", result );
    }

    @Test
    public void test_delete_multiple_mixins()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.any( Commands.mixin().delete().getClass() ) ) ).
            thenReturn( DeleteMixinResult.SUCCESS ).
            thenReturn( DeleteMixinResult.NOT_FOUND ).
            thenReturn( DeleteMixinResult.UNABLE_TO_DELETE );

        String result = resource().path( "schema/mixin/delete" ).entity( readFromFile( "delete_multiple_mixins_params.json" ),
                                                                         MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "delete_multiple_mixins.json", result );
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
