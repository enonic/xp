package com.enonic.wem.admin.rest.resource.schema.mixin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Files;

import com.enonic.wem.admin.json.schema.mixin.MixinListJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.json.AbstractMixinJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.json.MixinConfigJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.json.MixinCreateOrUpdateJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.json.MixinCreateOrUpdateParams;
import com.enonic.wem.admin.rest.resource.schema.mixin.json.MixinDeleteJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.json.MixinDeleteParams;
import com.enonic.wem.admin.rest.resource.schema.mixin.json.MixinGetJson;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_LINE;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MixinResourceTest
    extends JsonTestHelper
{
    private static QualifiedMixinName MY_MIXIN_QUALIFIED_NAME_1 = new QualifiedMixinName( "mymodule:input_text1" );

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
        this.client = Mockito.mock( Client.class );
        resource.setClient( client );

        this.uploadService = Mockito.mock( UploadService.class );
        this.resource.setUploadService( uploadService );

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
    public final void test_getMixin_existing_asJson()
        throws IOException
    {
        // setup
        Mixin mixin = Mixin.newMixin().module( MY_MIXIN_QUALIFIED_NAME_1.getModuleName() ).formItem(
            newInput().name( MY_MIXIN_QUALIFIED_NAME_1.getLocalName() ).inputType( TEXT_LINE ).label( "Line Text 1" ).required(
                true ).helpText( "Help text line 1" ).required( true ).build() ).build();

        Mockito.when( client.execute( mixin().get().names( QualifiedMixinNames.from( MY_MIXIN_QUALIFIED_NAME_1 ) ) ) ).thenReturn(
            Mixins.from( mixin ) );

        // execute
        AbstractMixinJson result = resource.get( MY_MIXIN_QUALIFIED_NAME_1.toString(), MixinResource.FORMAT_JSON );

        // verify
        assertTrue( result instanceof MixinGetJson );
        assertJsonEquals2( loadTestJson( "get_mixin_format_as_json-result.json" ), objectToJson( result ) );
    }

    @Test
    public final void test_getMixin_existing_asXml()
        throws IOException
    {
        // setup
        Mixin mixin = Mixin.newMixin().module( MY_MIXIN_QUALIFIED_NAME_1.getModuleName() ).formItem(
            newInput().name( MY_MIXIN_QUALIFIED_NAME_1.getLocalName() ).inputType( TEXT_LINE ).label( "Line Text 1" ).required(
                true ).helpText( "Help text line 1" ).required( true ).build() ).build();

        Mockito.when( client.execute( mixin().get().names( QualifiedMixinNames.from( MY_MIXIN_QUALIFIED_NAME_1 ) ) ) ).thenReturn(
            Mixins.from( mixin ) );

        // execute
        AbstractMixinJson result = resource.get( MY_MIXIN_QUALIFIED_NAME_1.toString(), MixinResource.FORMAT_XML );

        // verify
        assertTrue( result instanceof MixinConfigJson );
        assertJsonEquals2( loadTestJson( "get_mixin_format_as_xml-result.json" ), objectToJson( result ) );
    }

    @Test(expected = WebApplicationException.class)
    public final void test_getMixin_notFound()
        throws Exception
    {
        // setup
        Mockito.when( client.execute( Mockito.any( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        // execute
        resource.get( MY_MIXIN_QUALIFIED_NAME_1.toString(), MixinResource.FORMAT_JSON );
    }

    @Test(expected = WebApplicationException.class)
    public final void test_getMixin_wrongFormat()
    {
        // setup
        Mixin mixin = Mixin.newMixin().module( MY_MIXIN_QUALIFIED_NAME_1.getModuleName() ).formItem(
            newInput().name( MY_MIXIN_QUALIFIED_NAME_1.getLocalName() ).inputType( TEXT_LINE ).label( "Line Text 1" ).required(
                true ).helpText( "Help text line 1" ).required( true ).build() ).build();

        Mockito.when( client.execute( mixin().get().names( QualifiedMixinNames.from( MY_MIXIN_QUALIFIED_NAME_1 ) ) ) ).thenReturn(
            Mixins.from( mixin ) );

        // execute
        resource.get( MY_MIXIN_QUALIFIED_NAME_1.toString(), "not_existed_format" );
    }

    @Test
    public final void test_listMixins()
        throws IOException
    {
        // setup
        Mixin mixin1 = Mixin.newMixin().module( MY_MIXIN_QUALIFIED_NAME_1.getModuleName() ).formItem(
            newInput().name( MY_MIXIN_QUALIFIED_NAME_1.getLocalName() ).inputType( TEXT_LINE ).label( "Line Text 1" ).required(
                true ).helpText( "Help text line 1" ).required( true ).build() ).build();

        Mixin mixin2 = Mixin.newMixin().module( MY_MIXIN_QUALIFIED_NAME_2.getModuleName() ).formItem(
            newInput().name( MY_MIXIN_QUALIFIED_NAME_2.getLocalName() ).inputType( TEXT_AREA ).label( "Text Area" ).required(
                true ).helpText( "Help text area" ).required( true ).build() ).build();

        Mockito.when( client.execute( mixin().get().all() ) ).thenReturn( Mixins.from( mixin1, mixin2 ) );

        // execute
        MixinListJson result = this.resource.list();

        // verify
        assertJsonEquals2( loadTestJson( "list_mixins-result.json" ), objectToJson( result ) );
    }

    @Test
    public void test_createMixin()
        throws Exception
    {
        // setup
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        MixinCreateOrUpdateParams params = new MixinCreateOrUpdateParams();
        params.setMixin( loadTestFile( "create_mixin_xml-param.txt" ) );

        // execute
        MixinCreateOrUpdateJson result = resource.create( params );

        // verify
        assertJsonEquals2( loadTestJson( "create_mixin-result.json" ), objectToJson( result ) );
        verify( client, times( 1 ) ).execute( isA( CreateMixin.class ) );
    }

    @Test
    public void test_createMixin_withIcon()
        throws Exception
    {
        // setup
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        uploadFile( "edc1af66-ecb4-4f8a-8df4-0738418f84fc", "icon.png", IMAGE_DATA, "image/png" );

        MixinCreateOrUpdateParams params = new MixinCreateOrUpdateParams();
        params.setMixin( loadTestFile( "create_mixin_xml-param.txt" ) );
        params.setIconReference( "edc1af66-ecb4-4f8a-8df4-0738418f84fc" );

        // execute
        MixinCreateOrUpdateJson result = resource.create( params );

        // verify
        assertJsonEquals2( loadTestJson( "create_mixin-result.json" ), objectToJson( result ) );
        verify( client, times( 1 ) ).execute( isA( CreateMixin.class ) );
    }

    @Test
    public void test_updateMixin()
        throws Exception
    {
        // setup
        Mixin mixin = newMixin().module( Module.SYSTEM.getName() ).formItem(
            newInput().name( "some_input" ).inputType( InputTypes.TEXT_LINE ).build() ).build();

        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.from( mixin ) );

        MixinCreateOrUpdateParams params = new MixinCreateOrUpdateParams();
        params.setMixin( loadTestFile( "create_mixin_xml-param.txt" ) );

        // execute
        MixinCreateOrUpdateJson result = resource.update( params );

        // verify
        assertJsonEquals2( loadTestJson( "update_mixin-result.json" ), objectToJson( result ) );
        verify( client, times( 1 ) ).execute( isA( UpdateMixin.class ) );
    }

    @Test
    public void test_deleteMixin_single()
        throws Exception
    {
        // setup
        Mockito.when( client.execute( Mockito.any( Commands.mixin().delete().getClass() ) ) ).thenReturn( DeleteMixinResult.SUCCESS );

        MixinDeleteParams params = new MixinDeleteParams();
        params.setQualifiedMixinNames( Arrays.asList( "my:existing_mixin" ) );

        // execute
        MixinDeleteJson result = resource.delete( params );

        // verify
        assertJsonEquals2( loadTestJson( "delete_mixin_single-result.json" ), objectToJson( result ) );
    }

    @Test
    public void test_deleteMixin_Multiple()
        throws Exception
    {
        // setup
        Mockito.when( client.execute( Mockito.any( Commands.mixin().delete().getClass() ) ) ).
            thenReturn( DeleteMixinResult.SUCCESS ).
            thenReturn( DeleteMixinResult.NOT_FOUND ).
            thenReturn( DeleteMixinResult.UNABLE_TO_DELETE );

        MixinDeleteParams params = new MixinDeleteParams();
        params.setQualifiedMixinNames( Arrays.asList( "my:existing_mixin", "my:not_found_mixin", "my:being_used_mixin" ) );

        // execute
        MixinDeleteJson result = resource.delete( params );

        // verify
        assertJsonEquals2( loadTestJson( "delete_mixin_multiple-result.json" ), objectToJson( result ) );
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
