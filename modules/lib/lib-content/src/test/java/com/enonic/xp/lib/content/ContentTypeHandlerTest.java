package com.enonic.xp.lib.content;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.util.GenericValue;

import static org.mockito.ArgumentMatchers.any;

class ContentTypeHandlerTest
    extends BaseContentHandlerTest
{

    @Test
    void testExampleGetType()
    {
        final Form form = getExampleForm();
        Mockito.when( formFragmentService.inlineFormItems( Mockito.eq( form ) ) ).thenReturn( form );

        final ContentType contentType = exampleContentType();
        Mockito.when( contentTypeService.getByName( any() ) ).thenReturn( contentType );

        runScript( "/lib/xp/examples/content/getType.js" );
    }

    @Test
    void testExampleGetTypes()
    {
        final Form form = getForm();
        Mockito.when( formFragmentService.inlineFormItems( Mockito.eq( form ) ) ).thenReturn( form );

        final ContentTypes contentTypes = testContentTypes();
        Mockito.when( contentTypeService.getAll() ).thenReturn( contentTypes );

        runScript( "/lib/xp/examples/content/getTypes.js" );
    }

    @Test
    void testGet()
    {
        final Form form = getForm();
        Mockito.when( formFragmentService.inlineFormItems( Mockito.eq( form ) ) ).thenReturn( form );

        final ContentType contentType = testContentType();
        final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( contentType.getName() );
        Mockito.when( contentTypeService.getByName( any() ) ).thenReturn( contentType );

        runFunction( "/test/ContentTypeHandlerTest.js", "testGet" );
    }

    @Test
    void testGetNotFound()
    {
        runFunction( "/test/ContentTypeHandlerTest.js", "testGetNotFound" );
    }

    @Test
    void testGetNoName()
    {
        runFunction( "/test/ContentTypeHandlerTest.js", "testGetNoName" );
    }

    @Test
    void testList()
    {
        final Form form = getForm();
        Mockito.when( formFragmentService.inlineFormItems( Mockito.eq( form ) ) ).thenReturn( form );

        final ContentTypes contentTypes = testContentTypes();
        Mockito.when( contentTypeService.getAll() ).thenReturn( contentTypes );

        runFunction( "/test/ContentTypeHandlerTest.js", "testList" );
    }

    private ContentType exampleContentType()
    {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
        final Instant ts = LocalDateTime.of( 2016, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC );
        Icon schemaIcon = Icon.from( data, "image/png", ts );

        return ContentType.create()
            .name( "com.enonic.myapp:person" )
            .displayName( "Person" )
            .description( "Person content type" )
            .superType( ContentTypeName.structured() )
            .icon( schemaIcon )
            .form( getExampleForm() )
            .modifiedTime( Instant.parse( "2022-05-25T10:00:00.00Z" ) )
            .build();
    }

    private ContentType testContentType()
    {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
        final Instant ts = LocalDateTime.of( 2016, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC );
        Icon schemaIcon = Icon.from( data, "image/png", ts );

        return ContentType.create()
            .name( "com.enonic.myapp:article" )
            .displayName( "Article" )
            .description( "Article content type" )
            .superType( ContentTypeName.structured() )
            .icon( schemaIcon )
            .form( getForm() )
            .build();
    }

    private ContentTypes testContentTypes()
    {
        ContentType contentType1 = testContentType();
        ContentType contentType2 = ContentType.create()
            .name( "com.enonic.someapp:person" )
            .displayName( "Person" )
            .description( "Person content type" )
            .superType( ContentTypeName.structured() )
            .build();

        return ContentTypes.from( contentType1, contentType2 );
    }

    private Form getForm()
    {
        Input myTextLine = Input.create()
            .name( "myTextLine" )
            .inputType( InputTypeName.TEXT_LINE )
            .label( "My text line" )
            .helpText( "Some help text" )
            .required( true )
            .inputTypeConfig( GenericValue.newObject().put( "regexp", "\\b\\d{3}-\\d{2}-\\d{4}\\b" ).build() )
            .build();

        Input myCustomInput = Input.create()
            .name( "myCheckbox" )
            .inputType( InputTypeName.CHECK_BOX )
            .label( "My checkbox input" )
            .required( false )
            .inputTypeProperty( "default", "checked" )
            .build();

        Input radioButtonInput = Input.create()
            .name( "myRadioButton" )
            .inputType( InputTypeName.RADIO_BUTTON )
            .label( "Radio button" )
            .inputTypeProperty( "options", GenericValue.newList()
                .add( GenericValue.newObject()
                          .put( "value", "one" )
                          .put( "label", GenericValue.newObject().put( "text", "Value One" ).build() )
                          .build() )
                .add( GenericValue.newObject()
                          .put( "value", "two" )
                          .put( "label", GenericValue.newObject().put( "text", "Value Two" ).build() )
                          .build() )
                .build() )
            .inputTypeProperty( "theme", GenericValue.newList()
                .add( GenericValue.stringValue( "dark" ) )
                .add( GenericValue.stringValue( "light" ) )
                .build() )
            .inputTypeProperty( "disabled", GenericValue.booleanValue( false ) )
            .build();

        FieldSet myFieldSet = FieldSet.create()
            .label( "My field set" )
            .addFormItem( Input.create()
                              .name( "myTextLineInFieldSet" )
                              .inputType( InputTypeName.TEXT_LINE )
                              .label( "My text line" )
                              .required( false )
                              .build() )
            .build();

        FormItemSet myFormItemSet = FormItemSet.create()
            .name( "myFormItemSet" )
            .label( "My form item set" )
            .addFormItem(
                Input.create().name( "myTextLine" ).inputType( InputTypeName.TEXT_LINE ).label( "My text line" ).required( false ).build() )
            .build();

        final FormOptionSet formOptionSet = FormOptionSet.create()
            .name( "myOptionSet" )
            .label( "My option set" )
            .helpText( "Option set help text" )
            .addOptionSetOption( FormOptionSetOption.create()
                                     .name( "myOptionSetOption1" )
                                     .label( "option label1" )
                                     .helpText( "Option help text" )
                                     .addFormItem( Input.create()
                                                       .name( "myTextLine1" )
                                                       .label( "myTextLine1" )
                                                       .inputType( InputTypeName.TEXT_LINE )
                                                       .build() )
                                     .build() )
            .addOptionSetOption( FormOptionSetOption.create()
                                     .name( "myOptionSetOption2" )
                                     .label( "option label2" )
                                     .helpText( "Option help text" )
                                     .addFormItem( Input.create()
                                                       .name( "myTextLine2" )
                                                       .label( "myTextLine2" )
                                                       .inputType( InputTypeName.TEXT_LINE )
                                                       .build() )
                                     .build() )
            .build();

        return Form.create()
            .addFormItem( myTextLine )
            .addFormItem( myCustomInput )
            .addFormItem( radioButtonInput )
            .addFormItem( myFieldSet )
            .addFormItem( myFormItemSet )
            .addFormItem( formOptionSet )
            .build();
    }

    private Form getExampleForm()
    {
        Input name = Input.create().name( "name" ).inputType( InputTypeName.TEXT_LINE ).label( "Full name" ).required( true ).build();

        Input photo = Input.create()
            .name( "title" )
            .inputType( InputTypeName.IMAGE_SELECTOR )
            .label( "Photo" )
            .helpText( "Person photo" )
            .required( true )
            .build();

        Input bio = Input.create().name( "bio" ).inputType( InputTypeName.HTML_AREA ).label( "Bio" ).required( true ).build();

        Input email = Input.create()
            .name( "email" )
            .inputType( InputTypeName.TEXT_LINE )
            .label( "Email" )
            .helpText( "Email address" )
            .required( true )
            .inputTypeProperty( "regexp", "^[^@]+@[^@]+\\.[^@]+$" )
            .build();

        Input birthdate =
            Input.create().name( "birthdate" ).inputType( InputTypeName.DATE ).label( "Birth date" ).required( false ).build();

        Input nationality = Input.create()
            .name( "nationality" )
            .inputType( InputTypeName.CONTENT_SELECTOR )
            .inputTypeProperty( "allowContentType",
                                GenericValue.newList().add( GenericValue.stringValue( "com.enonic.myapp:country" ) ).build() )
            .label( "Nationality" )
            .build();

        return Form.create()
            .addFormItem( name )
            .addFormItem( photo )
            .addFormItem( bio )
            .addFormItem( birthdate )
            .addFormItem( email )
            .addFormItem( nationality )
            .build();
    }
}
