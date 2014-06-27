package com.enonic.wem.admin.rest.resource.schema.content;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.acme.DummyCustomInputType;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.MixinReference;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.CreateContentTypeParams;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.api.schema.content.UpdateContentTypeParams;

import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.form.inputtype.InputTypes.TEXT_LINE;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;


public class ContentTypeResourceTest
    extends AbstractResourceTest
{
    private ContentTypeService contentTypeService;

    private static final ContentTypeName MY_CTY_QUALIFIED_NAME = ContentTypeName.from( "my_cty" );

    public ContentTypeResourceTest()
    {
        super();
    }

    @Override
    protected Object getResourceInstance()
    {
        final ContentTypeResource resource = new ContentTypeResource();
        contentTypeService = Mockito.mock( ContentTypeService.class );
        resource.setContentTypeService( contentTypeService );

        return resource;
    }

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    @Test
    public void get_contentType_with_only_one_input()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            name( MY_CTY_QUALIFIED_NAME ).
            createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).
            superType( ContentTypeName.unstructured() ).
            displayName( "My ContentType" ).
            description( "My description" ).
            addFormItem( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( true ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( contentType ) );

        // execute
        String jsonString = request().path( "schema/content" ).queryParam( "name", MY_CTY_QUALIFIED_NAME.toString() ).queryParam(
            "mixinReferencesToFormItems", "false" ).get( String.class );

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_with_only_one_input-result.json", jsonString );
    }

    @Test
    public void get_contentType_with_all_formItem_types()
        throws Exception
    {
        // setup

        Input myTextLine = newInput().
            name( "myTextLine" ).
            inputType( TEXT_LINE ).
            label( "My text line" ).
            required( true ).
            build();

        Input myCustomInput = newInput().
            name( "myCustomInput" ).
            inputType( new DummyCustomInputType() ).
            label( "My custom input" ).
            required( false ).
            build();

        FieldSet myFieldSet = newFieldSet().
            name( "myFieldSet" ).
            label( "My field set" ).
            addFormItem( newInput().
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
            mixin( "mymixin" ).
            build();

        ContentType contentType = newContentType().
            createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).
            name( MY_CTY_QUALIFIED_NAME ).
            superType( ContentTypeName.unstructured() ).
            addFormItem( myTextLine ).
            addFormItem( myCustomInput ).
            addFormItem( myFieldSet ).
            addFormItem( myFormItemSet ).
            addFormItem( myMixinReference ).
            build();

        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( contentType ) );

        // execute
        String jsonString = request().path( "schema/content" ).queryParam( "name", MY_CTY_QUALIFIED_NAME.toString() ).queryParam(
            "mixinReferencesToFormItems", "false" ).get( String.class );

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_with_all_formItem_types-result.json", jsonString );
    }

    @Test
    public void get_contentType_with_format_as_xml()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).
            name( MY_CTY_QUALIFIED_NAME ).
            superType( ContentTypeName.unstructured() ).
            addFormItem( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( true ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( contentType ) );

        // execute
        String jsonString =
            request().path( "schema/content/config" ).queryParam( "name", MY_CTY_QUALIFIED_NAME.toString() ).get( String.class );

        // verify
        assertJson( "ContentTypeResourceTest-get_contentType_with_format_as_xml-result.json", jsonString );
    }

    @Test
    public void list_one_contentType_with_only_one_input()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).
            name( MY_CTY_QUALIFIED_NAME ).
            superType( ContentTypeName.unstructured() ).
            addFormItem( newInput().
                name( "myTextLine" ).
                inputType( TEXT_LINE ).
                label( "My text line" ).
                required( true ).
                build() ).
            build();

        Mockito.when( contentTypeService.getAll( Mockito.isA( GetAllContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( contentType ) );

        // execute
        String jsonString = request().
            path( "schema/content/all" ).
            queryParam( "names", MY_CTY_QUALIFIED_NAME.toString() ).
            queryParam( "format", "JSON" ).
            queryParam( "mixinReferencesToFormItems", "false" ).
            get( String.class );

        // verify
        assertJson( "ContentTypeResourceTest-list_one_contentType_with_only_one_input-result.json", jsonString );
    }

    @Test
    @Ignore
    public void test_create_new_content_type()
        throws Exception
    {
        Mockito.when( contentTypeService.getByName( Mockito.any( GetContentTypeParams.class ) ) ).thenReturn( null );
        ContentType createdContentType = ContentType.newContentType().
            name( "htmlarea" ).
            superType( ContentTypeName.structured() ).
            build();
        Mockito.when( contentTypeService.create( Mockito.any( CreateContentTypeParams.class ) ) ).thenReturn( createdContentType );

        String jsonString = request().path( "schema/content/create" ).entity( readFromFile( "create_content_type.json" ),
                                                                              MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "create_content_type_result.json", jsonString );
    }

    @Test
    @Ignore
    public void test_create_existing_content_type()
        throws Exception
    {
        Mockito.when( contentTypeService.getByName( Mockito.any( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.newContentType().name( "htmlarea" ).build() );
        String resultJson = request().path( "schema/content/create" ).entity( readFromFile( "create_content_type.json" ),
                                                                              MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "create_existing_content_type_result.json", resultJson );
    }

    @Test
    @Ignore
    public void test_create_content_type_with_broken_xml_config()
        throws Exception
    {
        String result = request().path( "schema/content/create" ).entity( readFromFile( "broken_xml_content_type.json" ),
                                                                          MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "create_content_type_with_broken_xml.json", result );
    }

    @Test
    @Ignore
    public void test_fail_to_create_new_content_type()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.any( GetContentTypesParams.class ) ) ).thenReturn( ContentTypes.empty() );
        Mockito.when( contentTypeService.create( Mockito.any( CreateContentTypeParams.class ) ) ).thenThrow(
            new RuntimeException( "Content type creation failed" ) );
        String result = request().path( "schema/content/create" ).entity( readFromFile( "create_content_type.json" ),
                                                                          MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "fail_to_create_new_content_type.json", result );
    }

    @Test
    public void test_update_content_type()
        throws Exception
    {
        ContentType contentType = ContentType.newContentType().
            name( "htmlarea" ).
            superType( ContentTypeName.structured() ).
            build();
        Mockito.when( contentTypeService.getByName( Mockito.any( GetContentTypeParams.class ) ) ).thenReturn( contentType );
        String jsonString = request().path( "schema/content/update" ).entity( readFromFile( "update_content_type.json" ),
                                                                              MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "update_content_type_result.json", jsonString );
    }

    @Test
    @Ignore
    public void test_update_content_type_with_broken_xml_config()
        throws Exception
    {
        String result = request().path( "schema/content/update" ).entity( readFromFile( "broken_xml_content_type.json" ),
                                                                          MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "update_content_type_with_broken_xml_config.json", result );

    }

    @Test
    @Ignore
    public void test_fail_to_update_content_type()
        throws Exception
    {
        Mockito.when( contentTypeService.update( Mockito.any( UpdateContentTypeParams.class ) ) ).thenThrow(
            new RuntimeException( "Content type update failed" ) );
        String result = request().path( "schema/content/update" ).entity( readFromFile( "update_content_type.json" ),
                                                                          MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "fail_to_update_content_type.json", result );
    }

}
