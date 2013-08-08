package com.enonic.wem.admin.rest.resource.schema.content;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.admin.rest.resource.schema.content.model.ContentTypeListJson;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.JsonTestHelper;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.content.form.FieldSet;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.MixinReference;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static com.enonic.wem.api.schema.content.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.schema.content.form.inputtype.InputTypes.TEXT_LINE;
import static junit.framework.Assert.assertTrue;

public class ContentTypeResourceTest
    extends JsonTestHelper
{
    private UploadService uploadService;

    private ContentTypeResource resource = new ContentTypeResource();

    private Client client;

    private static final QualifiedContentTypeName MY_CTY_QUALIFIED_NAME = QualifiedContentTypeName.from( "mymodule:my_cty" );

    public ContentTypeResourceTest()
    {
        super();
    }

    @Before
    public void setup()
    {
        client = Mockito.mock( Client.class );
        resource.setClient( client );

        uploadService = Mockito.mock( UploadService.class );
        resource.setUploadService( uploadService );

        mockCurrentContextHttpRequest();
    }

    @Test
    public void get_contentType_with_only_one_input()
    {
        // setup
        final ContentType contentType = newContentType().
            module( MY_CTY_QUALIFIED_NAME.getModuleName() ).
            name( MY_CTY_QUALIFIED_NAME.getLocalName() ).
            addFormItem( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( true ).
                build() ).
            build();

        Mockito.when( client.execute(
            Commands.contentType().get().qualifiedNames( QualifiedContentTypeNames.from( MY_CTY_QUALIFIED_NAME ) ) ) ).thenReturn(
            ContentTypes.from( contentType ) );

        // execute
        Object resultObject = resource.get( Lists.newArrayList( MY_CTY_QUALIFIED_NAME.toString() ), "JSON", true );

        // verify
        assertTrue( resultObject instanceof ContentTypeListJson );
        JsonNode actualJson = objectToJson( resultObject );
        assertJsonEquals2( loadTestJson( "get_contentType_with_only_one_input-result.json" ), actualJson );
    }

    @Test
    public void get_contentType_with_all_formItem_types()
    {
        // setup

        Input myTextLine = newInput().
            name( "myTextLine" ).
            inputType( TEXT_LINE ).
            label( "My text line" ).
            required( true ).
            build();

        FieldSet myFieldSet = newFieldSet().
            name( "myFieldSet" ).
            label( "My field set" ).
            add( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( false ).
                build() ).
            build();

        FormItemSet myFormItemSet = newFormItemSet().
            name( "myFormItemSet" ).
            label( "My form item set" ).
            addFormItem( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( false ).
                build() ).
            build();

        MixinReference myMixinReference = newMixinReference().
            name( "myMixinReference" ).
            mixin( "mymodule:mymixin" ).
            type( Input.class ).build();

        ContentType contentType = newContentType().
            module( MY_CTY_QUALIFIED_NAME.getModuleName() ).
            name( MY_CTY_QUALIFIED_NAME.getLocalName() ).
            addFormItem( myTextLine ).
            addFormItem( myFieldSet ).
            addFormItem( myFormItemSet ).
            addFormItem( myMixinReference ).
            build();

        Mockito.when( client.execute(
            Commands.contentType().get().qualifiedNames( QualifiedContentTypeNames.from( MY_CTY_QUALIFIED_NAME ) ) ) ).thenReturn(
            ContentTypes.from( contentType ) );

        // execute
        Object resultObject = resource.get( Lists.newArrayList( MY_CTY_QUALIFIED_NAME.toString() ), "JSON", true );

        // verify
        assertTrue( resultObject instanceof ContentTypeListJson );
        JsonNode actualJson = objectToJson( resultObject );
        assertJsonEquals2( loadTestJson( "get_contentType_with_all_formItem_types-result.json" ), actualJson );
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
    }
}
