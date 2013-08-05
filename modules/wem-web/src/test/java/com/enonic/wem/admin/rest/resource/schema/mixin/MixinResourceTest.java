package com.enonic.wem.admin.rest.resource.schema.mixin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.json.ObjectMapperHelper;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.AbstractMixinJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinConfigJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinGetJson;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
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
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_LINE;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class MixinResourceTest
{
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

    /**
     * Creates mock object for mixin.
     *
     * @return name of mocked mixin.
     */
    private String mockMixin()
    {
        String mixinName = "mymodule:mymixin";

        Input inputText1 = newInput().name( "input_text1" ).inputType( TEXT_LINE ).label( "Line Text 1" ).required( true )
            .helpText("Help text line 1" ).required( true ).build();
        Mixin mixin = Mixin.newMixin().module( ModuleName.from( "mymodule" ) ).formItem( inputText1 ).build();

        Mixins mixins = Mixins.from( mixin );
        QualifiedMixinNames names = QualifiedMixinNames.from( new QualifiedMixinName( mixinName ) );
        Mockito.when( client.execute( mixin().get().names( names ) ) ).thenReturn( mixins );

        return mixinName;
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
}
