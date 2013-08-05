package com.enonic.wem.admin.rest.resource.schema.mixin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.Files;

import com.enonic.wem.admin.json.ObjectMapperHelper;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.AbstractMixinJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinConfigJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinCreateOrUpdateJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinCreateOrUpdateParams;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinGetJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinListJson;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_LINE;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MixinResourceTest
{
    private static byte[] IMAGE_DATA =
        {0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte) 0x80, 0x0, 0x0, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x0, 0x0,
            0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b};

    private MixinResource resource;

    private Client client;

    private UploadService uploadService;

    @Before
    public void setup()
    {
        this.resource = new MixinResource();

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
        String mixinName = mockMixin();

        AbstractMixinJson result = resource.get( mixinName, MixinResource.FORMAT_JSON );

        assertNotNull( result );
        assertTrue( result instanceof MixinGetJson );
        assertTrue( isEqual( result, "get_mixin_asJson_result.json" ) );
    }

    @Test
    public final void test_getMixin_existing_asXml()
        throws IOException
    {
        String mixinName = mockMixin();

        AbstractMixinJson result = resource.get( mixinName, MixinResource.FORMAT_XML );

        assertNotNull( result );
        assertTrue( result instanceof MixinConfigJson );
        assertTrue( isEqual( result, "get_mixin_asXml_result.json" ) );
    }

    @Test(expected = WebApplicationException.class)
    public final void test_getMixin_notFound() throws Exception
    {
        Mockito.when( client.execute( Mockito.any( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        resource.get( "mymodule:mymixin", "json" );
    }

    @Test(expected = WebApplicationException.class)
    public final void test_getMixin_wrongFormat()
    {
        String mixinName = mockMixin();

        resource.get( mixinName, "not_existed_format" );
    }

    @Test
    public final void test_listMixins()
        throws IOException
    {
        mockMixinsList();

        MixinListJson result = this.resource.list();

        assertTrue( isEqual( result , "list_mixins_result.json" ));
    }

    @Test
    public void test_createMixin()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        MixinCreateOrUpdateParams params = new MixinCreateOrUpdateParams();
        params.setMixin( getFileAsString( "create_mixin_xml.txt" ) );
        MixinCreateOrUpdateJson result = resource.create( params );

        assertTrue( isEqual( result, "create_mixin_result.json" ) );

        verify( client, times( 1 ) ).execute( isA( CreateMixin.class ) );
    }

    @Test
    public void test_createMixin_withIcon()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetMixins.class ) ) ).thenReturn( Mixins.empty() );

        uploadFile( "edc1af66-ecb4-4f8a-8df4-0738418f84fc", "icon.png", IMAGE_DATA, "image/png" );

        MixinCreateOrUpdateParams params = new MixinCreateOrUpdateParams();
        params.setMixin( getFileAsString( "create_mixin_xml.txt" ) );
        params.setIconReference( "edc1af66-ecb4-4f8a-8df4-0738418f84fc" );
        MixinCreateOrUpdateJson result = resource.create( params );

        assertTrue( isEqual( result, "create_mixin_result.json" ) );

        verify( client, times( 1 ) ).execute( isA( CreateMixin.class ) );
    }

    /**
     * Creates mock object for mixin.
     *
     * @return name of mocked mixin.
     */
    private String mockMixin()
    {
        String mixinName = "mymodule:mymixin";

        Input inputText1 = newInput().name( "input_text1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true )
            .helpText( "Help text line 1" ).required( true ).build();
        Mixin mixin = Mixin.newMixin().module( ModuleName.from( "mymodule" ) ).formItem( inputText1 ).build();

        Mixins mixins = Mixins.from( mixin );
        QualifiedMixinNames names = QualifiedMixinNames.from( new QualifiedMixinName( mixinName ) );
        Mockito.when( client.execute( mixin().get().names( names ) ) ).thenReturn( mixins );

        return mixinName;
    }

    /**
     * Creates mock object for mixins list.
     */
    private void mockMixinsList()
    {
        Input inputText1 = newInput().name( "input_text1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true )
            .helpText( "Help text line 1" ).required( true ).build();
        Mixin mixin1 = Mixin.newMixin().module( ModuleName.from( "mymodule" ) ).formItem( inputText1 ).build();

        Input textArea1 = newInput().name( "text_area_1" ).inputType( TEXT_AREA ).label( "Text Area" ).required( true )
            .helpText("Help text area" ).required( true ).build();
        Mixin mixin2 = Mixin.newMixin().module( ModuleName.from( "othermodule" ) ).formItem( textArea1 ).build();

        Mixins mixins = Mixins.from( mixin1, mixin2 );
        Mockito.when( client.execute( mixin().get().all() ) ).thenReturn( mixins );
    }

    /**
     * Compares result value to expected value from file.
     *
     * @param actualValue value received from tested method.
     * @param resultFile file contained expected json result.
     *
     * @return result of json object comparison.
     *
     * @throws IOException if error occurred on reading file.
     */
    private boolean isEqual( Object actualValue, String resultFile )
        throws IOException
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();

        JsonNode expectedTree = mapper.readTree( getClass().getResource( resultFile ));
        JsonNode actualTree = mapper.valueToTree( actualValue );

        return actualTree.equals( expectedTree );
    }

    private String getFileAsString(String fileName)
        throws URISyntaxException, IOException
    {
        URI fileUri = getClass().getResource( fileName ).toURI();
        return Files.toString( new File( fileUri ), Charset.defaultCharset() );
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
