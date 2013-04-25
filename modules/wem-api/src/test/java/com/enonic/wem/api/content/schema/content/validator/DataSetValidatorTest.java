package com.enonic.wem.api.content.schema.content.validator;

import org.joda.time.DateMidnight;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.ValueTypes;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.inputtype.HtmlAreaConfig;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.content.schema.content.form.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static org.junit.Assert.*;


public class DataSetValidatorTest
{
    private ContentType contentType;

    @Before
    public void before()
    {
        contentType = newContentType().
            module( Module.SYSTEM.getName() ).
            name( "my_type" ).
            build();
    }

    @Test
    public void given_invalid_content_and_validator_that_recordExceptions_when_validate_then_no_exception_is_thrown()
    {
        // setup
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "Option 1", "o1" ).build();

        contentType.form().addFormItem(
            newInput().name( "mySingleSelector" ).inputType( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getContentData().setProperty( "mySingleSelector", new Value.Text( "nonExistingOption" ) );

        // exercise & verify
        DataSetValidator validator = new DataSetValidator( contentType );
        DataValidationErrors validationErrors = validator.validate( content.getContentData() );
        assertTrue( validationErrors.hasErrors() );
        assertEquals( "mySingleSelector", validationErrors.getFirst().getPath().toString() );
    }

    @Test
    public void given_invalid_content_and_validator_that_recordExceptions_when_validate_then_getInvalidDataExceptions_returns_exceptions()
    {
        // setup
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeRadio().addOption( "Option 1", "o1" ).build();

        contentType.form().addFormItem( newInput().name( "mySingleSelector1" ).inputType( InputTypes.SINGLE_SELECTOR ).inputTypeConfig(
            singleSelectorConfig ).build() );
        contentType.form().addFormItem( newInput().name( "mySingleSelector2" ).inputType( InputTypes.SINGLE_SELECTOR ).inputTypeConfig(
            singleSelectorConfig ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getContentData().setProperty( "mySingleSelector1", new Value.Text( "nonExistingOption" ) );
        content.getContentData().setProperty( "mySingleSelector2", new Value.Text( "nonExistingOption" ) );

        // exercise & verify
        DataSetValidator validator = new DataSetValidator( contentType );
        DataValidationErrors validationErrors = validator.validate( content.getContentData() );
        assertTrue( validationErrors.hasErrors() );
        assertEquals( 2, validationErrors.size() );
    }

    @Test
    public void given_valid_content_with_all_inputs_when_validate_then_no_exception_is_thrown()
    {
        // setup
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "Option 1", "o1" ).build();
        HtmlAreaConfig htmlAreaConfig = HtmlAreaConfig.newHtmlAreaConfig().build();

        contentType.form().addFormItem( newInput().name( "myDate" ).inputType( InputTypes.DATE ).build() );
        contentType.form().addFormItem( newInput().name( "myDecimalNumber" ).inputType( InputTypes.DECIMAL_NUMBER ).build() );
        contentType.form().addFormItem(
            newInput().name( "mySingleSelector" ).inputType( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );
        contentType.form().addFormItem(
            newInput().name( "myHtmlArea" ).inputType( InputTypes.HTML_AREA ).inputTypeConfig( htmlAreaConfig ).build() );
        contentType.form().addFormItem( newInput().name( "myPhone" ).inputType( InputTypes.PHONE ).build() );
        contentType.form().addFormItem( newInput().name( "myTextArea" ).inputType( InputTypes.TEXT_AREA ).build() );
        contentType.form().addFormItem( newInput().name( "myTextLine" ).inputType( InputTypes.TEXT_LINE ).build() );
        contentType.form().addFormItem( newInput().name( "myWholeNumber" ).inputType( InputTypes.WHOLE_NUMBER ).build() );
        contentType.form().addFormItem( newInput().name( "myXml" ).inputType( InputTypes.XML ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getContentData().setProperty( "myDate", new Value.Date( new DateMidnight( 2012, 9, 11 ) ) );
        content.getContentData().setProperty( "myDecimalNumber", new Value.DecimalNumber( 12.34 ) );
        content.getContentData().setProperty( "mySingleSelector", new Value.Text( "o1" ) );
        content.getContentData().setProperty( "myHtmlArea", new Value.HtmlPart( "<h1>Hello world</h1>" ) );

        // exercise
        DataSetValidator validator = new DataSetValidator( contentType );
        DataValidationErrors validationErrors = validator.validate( content.getContentData() );
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    public void given_untyped_content_when_setting_type_that_fits_then_everything_validates()
    {
        // setup
        ContentData contentData = new ContentData();
        contentData.setProperty( "name", new Value.Text( "Thomas" ) );
        contentData.setProperty( "personalia.eyeColour", new Value.Text( "Blue" ) );
        contentData.setProperty( "personalia.hairColour", new Value.Text( "Blonde" ) );
        contentData.setProperty( "crimes[0].description", new Value.Text( "Stole tomatoes from neighbour" ) );
        contentData.setProperty( "crimes[0].year", new Value.Text( "1989" ) );
        contentData.setProperty( "crimes[1].description", new Value.Text( "Stole a chocolate from the Matbua shop" ) );
        contentData.setProperty( "crimes[1].year", new Value.Text( "1990" ) );

        assertEquals( ValueTypes.TEXT, contentData.getProperty( "personalia.eyeColour" ).getType() );
        Assert.assertEquals( "Blue", contentData.getProperty( "personalia.eyeColour" ).getObject() );
        Assert.assertEquals( "personalia.eyeColour", contentData.getProperty( "personalia.eyeColour" ).getPath().toString() );

        // exercise
        contentType.form().addFormItem( newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() );
        FormItemSet personalia = newFormItemSet().name( "personalia" ).multiple( false ).build();
        contentType.form().addFormItem( personalia );
        personalia.add( newInput().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() );
        personalia.add( newInput().name( "hairColour" ).inputType( InputTypes.TEXT_LINE ).build() );
        FormItemSet crimes = newFormItemSet().name( "crimes" ).multiple( true ).build();
        contentType.form().addFormItem( crimes );
        crimes.add( newInput().name( "description" ).inputType( InputTypes.TEXT_LINE ).build() );
        crimes.add( newInput().name( "year" ).inputType( InputTypes.TEXT_LINE ).build() );

        assertEquals( ValueTypes.TEXT, contentData.getProperty( "personalia.eyeColour" ).getType() );
        Assert.assertEquals( "Blue", contentData.getProperty( "personalia.eyeColour" ).getObject() );

        // verify
        DataSetValidator validator = new DataSetValidator( contentType );
        DataValidationErrors validationErrors = validator.validate( contentData );
        assertFalse( validationErrors.hasErrors() );
    }

}
