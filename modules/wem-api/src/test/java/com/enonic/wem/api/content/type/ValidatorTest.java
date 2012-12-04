package com.enonic.wem.api.content.type;

import org.joda.time.DateMidnight;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InvalidDataTypeException;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.form.BreaksRegexValidationException;
import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.InvalidDataException;
import com.enonic.wem.api.content.type.form.InvalidValueException;
import com.enonic.wem.api.content.type.form.inputtype.HtmlAreaConfig;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.content.type.form.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.Validator.newValidator;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;


public class ValidatorTest
{
    private ContentType contentType;

    @Before
    public void before()
    {
        contentType = newContentType().
            module( Module.SYSTEM.getName() ).
            name( "MyType" ).
            build();
    }

    @Test
    public void given_invalid_content_and_validator_that_recordExceptions_when_validate_then_no_exception_is_thrown()
    {
        // setup
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "Option 1", "o1" ).build();

        contentType.form().addFormItem(
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "mySingleSelector", "nonExistingOption" );

        // exercise & verify
        Validator validator = newValidator().contentType( contentType ).recordExceptions( true ).build();
        try
        {
            validator.validate( content.getData() );
        }
        catch ( Throwable e )
        {
            fail( "Validator is not supposed to throw any exception" );
        }
    }

    @Test
    public void given_invalid_content_and_validator_that_recordExceptions_when_validate_then_getInvalidDataExceptions_returns_exceptions()
    {
        // setup
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeRadio().addOption( "Option 1", "o1" ).build();

        contentType.form().addFormItem(
            newInput().name( "mySingleSelector1" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );
        contentType.form().addFormItem(
            newInput().name( "mySingleSelector2" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "mySingleSelector1", "nonExistingOption" );
        content.setData( "mySingleSelector2", "nonExistingOption" );

        // exercise & verify
        Validator validator = newValidator().contentType( contentType ).recordExceptions( true ).build();
        try
        {
            validator.validate( content.getData() );
        }
        catch ( Throwable e )
        {
            fail( "Validator is not supposed to throw any exception" );
        }
        assertEquals( 2, validator.getInvalidDataExceptions().size() );
    }

    @Test
    public void given_valid_content_with_all_inputs_when_validate_then_no_exception_is_thrown()
    {
        // setup
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "Option 1", "o1" ).build();
        HtmlAreaConfig htmlAreaConfig = HtmlAreaConfig.newHtmlAreaConfig().build();

        contentType.form().addFormItem( newInput().name( "myColor" ).type( InputTypes.COLOR ).build() );
        contentType.form().addFormItem( newInput().name( "myDate" ).type( InputTypes.DATE ).build() );
        contentType.form().addFormItem( newInput().name( "myDecimalNumber" ).type( InputTypes.DECIMAL_NUMBER ).build() );
        contentType.form().addFormItem(
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );
        contentType.form().addFormItem( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() );
        contentType.form().addFormItem(
            newInput().name( "myHtmlArea" ).type( InputTypes.HTML_AREA ).inputTypeConfig( htmlAreaConfig ).build() );
        contentType.form().addFormItem( newInput().name( "myPhone" ).type( InputTypes.PHONE ).build() );
        contentType.form().addFormItem( newInput().name( "myTextArea" ).type( InputTypes.TEXT_AREA ).build() );
        contentType.form().addFormItem( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        contentType.form().addFormItem( newInput().name( "myWholeNumber" ).type( InputTypes.WHOLE_NUMBER ).build() );
        contentType.form().addFormItem( newInput().name( "myXml" ).type( InputTypes.XML ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myColor.red", 0l );
        content.setData( "myColor.blue", 0l );
        content.setData( "myColor.green", 0l );
        content.setData( "myDate", new DateMidnight( 2012, 9, 11 ) );
        content.setData( "myDecimalNumber", 12.34 );
        content.setData( "mySingleSelector", "o1" );
        content.setData( "myGeoLocation.latitude", 0.0 );
        content.setData( "myGeoLocation.longitude", 0.0 );
        content.setData( "myHtmlArea", "<h1>Hello world</h1>" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validator.validate( content.getData() );
    }

    @Test
    public void given_invalid_data_according_to_inputs_validationRegex_when_validate_then_exception()
    {
        // setup
        contentType.form().addFormItem( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).validationRegexp( "a*c" ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myTextLine", "aax" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).checkValidationRegexp( true ).build();
        validateAndAssertInvalidDataException( validator, content, BreaksRegexValidationException.class, content.getData( "myTextLine" ) );
    }

    @Test
    public void given_content_with_invalid_dataSet_according_to_input_inside_a_formItemSet_when_validate_then_exception_is_thrown()
    {
        // setup
        FormItemSet formItemSet = newFormItemSet().name( "mySet" ).build();
        contentType.form().addFormItem( formItemSet );
        formItemSet.add( newInput().name( "myColor" ).type( InputTypes.COLOR ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "mySet.myColor.red", 0l );
        content.setData( "mySet.myColor.green", 0l );
        content.setData( "mySet.myColor.blue", -1l );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class, content.getData( "mySet.myColor" ) );

    }

    @Test
    public void given_content_with_invalid_dataSet_according_to_input_inside_a_layout_when_validate_then_exception_is_thrown()
    {
        // setup
        FieldSet layout = FieldSet.newFieldSet().name( "mySet" ).label( "Label" ).build();
        contentType.form().addFormItem( layout );
        layout.addFormItem( newInput().name( "myColor" ).type( InputTypes.COLOR ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myColor.red", 0l );
        content.setData( "myColor.green", 0l );
        content.setData( "myColor.blue", -1l );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class, content.getData( "myColor" ) );

    }

    @Test
    public void given_content_with_invalid_dataSet_according_to_dataType_inside_a_formItemSet_when_validate_then_exception_is_thrown()
    {
        // setup
        FormItemSet formItemSet = newFormItemSet().name( "mySet" ).build();
        contentType.form().addFormItem( formItemSet );
        formItemSet.add( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "mySet.myGeoLocation.latitude", 0.0 );
        content.setData( "mySet.myGeoLocation.longitude", -181.00 );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class, content.getData( "mySet.myGeoLocation" ) );
    }

    @Test
    public void given_content_with_invalid_data_according_to_dataType_inside_a_formItemSet_when_validate_then_exception_is_thrown()
    {
        // setup
        FormItemSet formItemSet = newFormItemSet().name( "mySet" ).build();
        contentType.form().addFormItem( formItemSet );
        formItemSet.add( newInput().name( "myDate" ).type( InputTypes.DATE ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "mySet.myDate", "2000-01-01" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueTypeException.class, content.getData( "mySet.myDate" ) );
    }

    @Test
    public void given_nonExistingValue_for_radio_button_when_checkValidity_then_InvalidDataException_is_thrown()
    {
        // setup
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "Option 1", "o1" ).build();

        contentType.form().addFormItem(
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "mySingleSelector", "nonExistingOption" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class, content.getData( "mySingleSelector" ) );
    }

    @Test
    public void given_illegal_type_for_longitude_when_checkValidity_then_InvalidDataException_is_thrown()
    {
        // setup
        contentType.form().addFormItem( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myGeoLocation.latitude", 0.0 );
        content.setData( "myGeoLocation.longitude", "0.0" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidDataTypeException.class, content.getData( "myGeoLocation" ) );
    }

    @Test
    public void given_illegal_value_for_a_dataSet_defined_by_a_dataType_when_checkValidity_then_InvalidDataException_is_thrown()
    {
        // setup:
        contentType.form().addFormItem( newInput().name( "myGeoLocation" ).type( InputTypes.GEO_LOCATION ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "myGeoLocation.latitude", 0.0 );
        content.setData( "myGeoLocation.longitude", -181.00 );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class, content.getData( "myGeoLocation" ) );
    }

    @Test
    public void given_untyped_content_when_setting_type_that_fits_then_everything_validates()
    {
        // setup
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );
        content.setData( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.setData( "crimes[0].year", "1989" );
        content.setData( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.setData( "crimes[1].year", "1990" );

        assertEquals( DataTypes.TEXT, content.getData( "personalia.eyeColour" ).getDataType() );
        Assert.assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        Assert.assertEquals( "personalia.eyeColour", content.getData( "personalia.eyeColour" ).getPath().toString() );

        // exercise
        contentType.form().addFormItem( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        FormItemSet personalia = newFormItemSet().name( "personalia" ).multiple( false ).build();
        contentType.form().addFormItem( personalia );
        personalia.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        personalia.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );
        FormItemSet crimes = newFormItemSet().name( "crimes" ).multiple( true ).build();
        contentType.form().addFormItem( crimes );
        crimes.add( newInput().name( "description" ).type( InputTypes.TEXT_LINE ).build() );
        crimes.add( newInput().name( "year" ).type( InputTypes.TEXT_LINE ).build() );

        assertEquals( DataTypes.TEXT, content.getData( "personalia.eyeColour" ).getDataType() );
        Assert.assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );

        // verify
        Validator validator = newValidator().contentType( contentType ).build();
        validator.validate( content.getData() );
    }

    private void validateAndAssertInvalidDataException( Validator validator, Content content, Class cause, Data data )
    {
        try
        {
            validator.validate( content.getData() );
            fail( "Expected " + InvalidDataException.class.getSimpleName() );
        }
        catch ( Exception e )
        {
            assertTrue( "Expected exception " + InvalidDataException.class.getSimpleName(), InvalidDataException.class.isInstance( e ) );
            assertTrue( "Expected cause " + cause.getSimpleName(), cause.isInstance( e.getCause() ) );
            InvalidDataException invalidDataException = (InvalidDataException) e;
            assertEquals( data.getPath(), invalidDataException.getData().getPath() );
        }
    }
}
