package com.enonic.xp.lib.content;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetAllContentTypesParams;
import com.enonic.xp.schema.content.GetContentTypeParams;

public class ContentTypeHandlerTest
    extends BaseContentHandlerTest
{

    @Test
    public void testExampleGetType()
    {
        final Form form = getExampleForm();
        Mockito.when( mixinService.inlineFormItems( Mockito.eq( form ) ) ).thenReturn( form );

        final ContentType contentType = exampleContentType();
        final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( contentType.getName() ).
            inlineMixinsToFormItems( true );
        Mockito.when( contentTypeService.getByName( params ) ).thenReturn( contentType );

        runScript( "/site/lib/xp/examples/content/getType.js" );
    }

    @Test
    public void testExampleGetTypes()
    {
        final Form form = getForm();
        Mockito.when( mixinService.inlineFormItems( Mockito.eq( form ) ) ).thenReturn( form );

        final ContentTypes contentTypes = testContentTypes();
        Mockito.when( contentTypeService.getAll( Mockito.isA( GetAllContentTypesParams.class ) ) ).thenReturn( contentTypes );

        runScript( "/site/lib/xp/examples/content/getTypes.js" );
    }

    @Test
    public void testGet()
        throws Exception
    {
        final Form form = getForm();
        Mockito.when( mixinService.inlineFormItems( Mockito.eq( form ) ) ).thenReturn( form );

        final ContentType contentType = testContentType();
        final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( contentType.getName() ).
            inlineMixinsToFormItems( true );
        Mockito.when( contentTypeService.getByName( params ) ).thenReturn( contentType );

        runFunction( "/site/test/ContentTypeHandlerTest.js", "testGet" );
    }

    @Test
    public void testGetNotFound()
        throws Exception
    {
        runFunction( "/site/test/ContentTypeHandlerTest.js", "testGetNotFound" );
    }

    @Test
    public void testGetNoName()
        throws Exception
    {
        runFunction( "/site/test/ContentTypeHandlerTest.js", "testGetNoName" );
    }

    @Test
    public void testList()
        throws Exception
    {
        final Form form = getForm();
        Mockito.when( mixinService.inlineFormItems( Mockito.eq( form ) ) ).thenReturn( form );

        final ContentTypes contentTypes = testContentTypes();
        Mockito.when( contentTypeService.getAll( Mockito.isA( GetAllContentTypesParams.class ) ) ).thenReturn( contentTypes );

        runFunction( "/site/test/ContentTypeHandlerTest.js", "testList" );
    }

    private ContentType exampleContentType()
    {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
        final Instant ts = LocalDateTime.of( 2016, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC );
        Icon schemaIcon = Icon.from( data, "image/png", ts );

        return ContentType.create().
            name( "com.enonic.myapp:person" ).
            displayName( "Person" ).
            description( "Person content type" ).
            superType( ContentTypeName.structured() ).
            contentDisplayNameScript( "$('name')" ).
            icon( schemaIcon ).
            form( getExampleForm() ).
            build();
    }

    private ContentType testContentType()
    {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
        final Instant ts = LocalDateTime.of( 2016, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC );
        Icon schemaIcon = Icon.from( data, "image/png", ts );

        return ContentType.create().
            name( "com.enonic.myapp:article" ).
            displayName( "Article" ).
            description( "Article content type" ).
            superType( ContentTypeName.structured() ).
            contentDisplayNameScript( "$('title') + ' ' + $('author')" ).
            icon( schemaIcon ).
            form( getForm() ).
            build();
    }

    private ContentTypes testContentTypes()
    {
        ContentType contentType1 = testContentType();
        ContentType contentType2 = ContentType.create().
            name( "com.enonic.someapp:person" ).
            displayName( "Person" ).
            description( "Person content type" ).
            superType( ContentTypeName.structured() ).
            build();

        return ContentTypes.from( contentType1, contentType2 );
    }

    private Form getForm()
    {
        Input myTextLine = Input.create().
            name( "myTextLine" ).
            inputType( InputTypeName.TEXT_LINE ).
            label( "My text line" ).
            customText( "Some custom text" ).
            helpText( "Some help text" ).
            required( true ).
            inputTypeConfig( InputTypeConfig.create().property( InputTypeProperty.create( "regexp", "\\b\\d{3}-\\d{2}-\\d{4}\\b" ).
                build() ).build() ).
            build();

        Input myCustomInput = Input.create().
            name( "myCheckbox" ).
            inputType( InputTypeName.CHECK_BOX ).
            label( "My checkbox input" ).
            required( false ).
            defaultValue( InputTypeDefault.create().property( InputTypeProperty.create( "default", "checked" ).build() ).build() ).
            build();

        final InputTypeProperty option1 = InputTypeProperty.create( "option", "Option One" ).attribute( "value", "one" ).build();
        final InputTypeProperty option2 = InputTypeProperty.create( "option", "Option Two" ).attribute( "value", "two" ).build();
        Input radioButtonInput = Input.create().
            name( "myRadioButton" ).
            inputType( InputTypeName.RADIO_BUTTON ).
            label( "Radio button" ).
            inputTypeConfig( InputTypeConfig.create().property( option1 ).property( option2 ).build() ).
            build();

        FieldSet myFieldSet = FieldSet.create().
            name( "myFieldSet" ).
            label( "My field set" ).
            addFormItem( Input.create().
                name( "myTextLineInFieldSet" ).
                inputType( InputTypeName.TEXT_LINE ).
                label( "My text line" ).
                required( false ).
                build() ).
            build();

        FormItemSet myFormItemSet = FormItemSet.create().
            name( "myFormItemSet" ).
            label( "My form item set" ).
            addFormItem( Input.create().
                name( "myTextLine" ).
                inputType( InputTypeName.TEXT_LINE ).
                label( "My text line" ).
                required( false ).
                build() ).
            build();

        final FormOptionSet formOptionSet = FormOptionSet.create().
            name( "myOptionSet" ).
            label( "My option set" ).
            helpText( "Option set help text" ).
            addOptionSetOption(
                FormOptionSetOption.create().name( "myOptionSetOption1" ).label( "option label1" ).helpText( "Option help text" ).
                    addFormItem( Input.create().name( "myTextLine1" ).label( "myTextLine1" ).inputType(
                        InputTypeName.TEXT_LINE ).build() ).build() ).
            addOptionSetOption(
                FormOptionSetOption.create().name( "myOptionSetOption2" ).label( "option label2" ).helpText( "Option help text" ).
                    addFormItem( Input.create().name( "myTextLine2" ).label( "myTextLine2" ).inputType(
                        InputTypeName.TEXT_LINE ).build() ).build() ).
            build();

        return Form.create().
            addFormItem( myTextLine ).
            addFormItem( myCustomInput ).
            addFormItem( radioButtonInput ).
            addFormItem( myFieldSet ).
            addFormItem( myFormItemSet ).
            addFormItem( formOptionSet ).
            build();
    }

    private Form getExampleForm()
    {
        Input name = Input.create().
            name( "name" ).
            inputType( InputTypeName.TEXT_LINE ).
            label( "Full name" ).
            required( true ).
            build();

        Input photo = Input.create().
            name( "title" ).
            inputType( InputTypeName.IMAGE_SELECTOR ).
            label( "Photo" ).
            helpText( "Person photo" ).
            required( true ).
            build();

        Input bio = Input.create().
            name( "bio" ).
            inputType( InputTypeName.HTML_AREA ).
            label( "Bio" ).
            required( true ).
            build();

        Input email = Input.create().
            name( "email" ).
            inputType( InputTypeName.TEXT_LINE ).
            label( "Email" ).
            helpText( "Email address" ).
            required( true ).
            inputTypeConfig( InputTypeConfig.create().property( InputTypeProperty.create( "regexp", "^[^@]+@[^@]+\\.[^@]+$" ).
                build() ).build() ).
            build();

        Input birthdate = Input.create().
            name( "birthdate" ).
            inputType( InputTypeName.DATE ).
            label( "Birth date" ).
            required( false ).
            build();

        Input nationality = Input.create().
            name( "nationality" ).
            inputType( InputTypeName.CONTENT_SELECTOR ).
            inputTypeProperty( InputTypeProperty.create( "allowedContentType", "com.enonic.myapp:country" ).build() ).
            label( "Nationality" ).
            build();

        return Form.create().
            addFormItem( name ).
            addFormItem( photo ).
            addFormItem( bio ).
            addFormItem( birthdate ).
            addFormItem( email ).
            addFormItem( nationality ).
            build();
    }
}
