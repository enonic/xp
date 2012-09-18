package com.enonic.wem.core.content.type;

import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.core.content.Content;
import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.datatype.InvalidValueTypeException;
import com.enonic.wem.core.content.type.formitem.BreaksRegexValidationException;
import com.enonic.wem.core.content.type.formitem.FormItemSet;
import com.enonic.wem.core.content.type.formitem.InvalidDataException;
import com.enonic.wem.core.content.type.formitem.InvalidValueException;
import com.enonic.wem.core.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.core.content.type.formitem.comptype.DropdownConfig;
import com.enonic.wem.core.content.type.formitem.comptype.HtmlAreaConfig;
import com.enonic.wem.core.content.type.formitem.comptype.RadioButtonsConfig;

import static com.enonic.wem.core.content.type.Validator.newValidator;
import static com.enonic.wem.core.content.type.formitem.Component.newComponent;
import static com.enonic.wem.core.content.type.formitem.FormItemSet.newFormItemSet;
import static com.enonic.wem.core.content.type.formitem.comptype.DropdownConfig.newDropdownConfig;
import static com.enonic.wem.core.content.type.formitem.comptype.HtmlAreaConfig.newHtmlAreaConfig;
import static com.enonic.wem.core.content.type.formitem.comptype.RadioButtonsConfig.newRadioButtonsConfig;
import static org.junit.Assert.*;


public class ValidatorTest
{

    @Test
    public void given_invalid_content_and_validator_that_recordExceptions_when_validate_then_no_exception_is_thrown()
    {
        // setup
        RadioButtonsConfig radioButtonsConfig = newRadioButtonsConfig().addOption( "Option 1", "o1" ).build();

        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myRadioButtons" ).type( ComponentTypes.RADIO_BUTTONS ).componentTypeConfig(
            radioButtonsConfig ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myRadioButtons", "nonExistingOption" );

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
        RadioButtonsConfig radioButtonsConfig = newRadioButtonsConfig().addOption( "Option 1", "o1" ).build();

        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myRadioButtons1" ).type( ComponentTypes.RADIO_BUTTONS ).componentTypeConfig(
            radioButtonsConfig ).build() );
        contentType.addFormItem( newComponent().name( "myRadioButtons2" ).type( ComponentTypes.RADIO_BUTTONS ).componentTypeConfig(
            radioButtonsConfig ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myRadioButtons1", "nonExistingOption" );
        content.setData( "myRadioButtons2", "nonExistingOption" );

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
    public void given_valid_content_with_all_components_when_validate_then_no_exception_is_thrown()
    {
        // setup
        DropdownConfig dropdownConfig = newDropdownConfig().addOption( "Option 1", "o1" ).build();
        RadioButtonsConfig radioButtonsConfig = newRadioButtonsConfig().addOption( "Radio 1", "r1" ).build();
        HtmlAreaConfig htmlAreaConfig = newHtmlAreaConfig().build();

        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myColor" ).type( ComponentTypes.COLOR ).build() );
        contentType.addFormItem( newComponent().name( "myDate" ).type( ComponentTypes.DATE ).build() );
        contentType.addFormItem( newComponent().name( "myDecimalNumber" ).type( ComponentTypes.DECIMAL_NUMBER ).build() );
        contentType.addFormItem(
            newComponent().name( "myDropdown" ).type( ComponentTypes.DROPDOWN ).componentTypeConfig( dropdownConfig ).build() );
        contentType.addFormItem( newComponent().name( "myGeoLocation" ).type( ComponentTypes.GEO_LOCATION ).build() );
        contentType.addFormItem(
            newComponent().name( "myHtmlArea" ).type( ComponentTypes.HTML_AREA ).componentTypeConfig( htmlAreaConfig ).build() );
        contentType.addFormItem( newComponent().name( "myPhone" ).type( ComponentTypes.PHONE ).build() );
        contentType.addFormItem( newComponent().name( "myRadioButtons" ).type( ComponentTypes.RADIO_BUTTONS ).componentTypeConfig(
            radioButtonsConfig ).build() );
        contentType.addFormItem( newComponent().name( "myTextArea" ).type( ComponentTypes.TEXT_AREA ).build() );
        contentType.addFormItem( newComponent().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).build() );
        contentType.addFormItem( newComponent().name( "myWholeNumber" ).type( ComponentTypes.WHOLE_NUMBER ).build() );
        contentType.addFormItem( newComponent().name( "myXml" ).type( ComponentTypes.XML ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myColor.red", 0l );
        content.setData( "myColor.blue", 0l );
        content.setData( "myColor.green", 0l );
        content.setData( "myDate", new DateMidnight( 2012, 9, 11 ) );
        content.setData( "myDecimalNumber", 12.34 );
        content.setData( "myDropdown", "o1" );
        content.setData( "myGeoLocation.latitude", 0.0 );
        content.setData( "myGeoLocation.longitude", 0.0 );
        content.setData( "myHtmlArea", "<h1>Hello world</h1>" );
        content.setData( "myRadioButtons", "r1" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validator.validate( content.getData() );
    }


    @Test
    public void given_invalid_data_according_to_components_validationRegex_when_validate_then_exception()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).validationRegexp( "a*c" ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myTextLine", "aax" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).checkValidationRegexp( true ).build();
        validateAndAssertInvalidDataException( validator, content, BreaksRegexValidationException.class, content.getData( "myTextLine" ) );
    }

    @Test
    public void given_content_with_invalid_dataSet_according_to_component_inside_a_formItemSet_when_validate_then_exception_is_thrown()
    {
        // setup
        ContentType contentType = new ContentType();
        FormItemSet formItemSet = FormItemSet.newFormItemSet().name( "myFormItemSet" ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.addFormItem( newComponent().name( "myColor" ).type( ComponentTypes.COLOR ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myFormItemSet.myColor.red", 0l );
        content.setData( "myFormItemSet.myColor.green", 0l );
        content.setData( "myFormItemSet.myColor.blue", -1l );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class,
                                               content.getData( "myFormItemSet.myColor" ) );

    }

    @Test
    public void given_content_with_invalid_dataSet_according_to_dataType_inside_a_formItemSet_when_validate_then_exception_is_thrown()
    {
        // setup
        ContentType contentType = new ContentType();
        FormItemSet formItemSet = FormItemSet.newFormItemSet().name( "myFormItemSet" ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.addFormItem( newComponent().name( "myGeoLocation" ).type( ComponentTypes.GEO_LOCATION ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myFormItemSet.myGeoLocation.latitude", 0.0 );
        content.setData( "myFormItemSet.myGeoLocation.longitude", -181.00 );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class,
                                               content.getData( "myFormItemSet.myGeoLocation" ) );
    }

    @Test
    public void given_content_with_invalid_data_according_to_dataType_inside_a_formItemSet_when_validate_then_exception_is_thrown()
    {
        // setup
        ContentType contentType = new ContentType();
        FormItemSet formItemSet = FormItemSet.newFormItemSet().name( "myFormItemSet" ).build();
        contentType.addFormItem( formItemSet );
        formItemSet.addFormItem( newComponent().name( "myDate" ).type( ComponentTypes.DATE ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myFormItemSet.myDate", "2000-01-01" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueTypeException.class,
                                               content.getData( "myFormItemSet.myDate" ) );
    }

    @Test
    public void given_nonExistingValue_for_radio_button_when_checkValidity_then_InvalidDataException_is_thrown()
    {
        // setup
        RadioButtonsConfig radioButtonsConfig = newRadioButtonsConfig().addOption( "Option 1", "o1" ).build();

        ContentType contentType = new ContentType();

        contentType.addFormItem( newComponent().name( "myRadioButtons" ).type( ComponentTypes.RADIO_BUTTONS ).componentTypeConfig(
            radioButtonsConfig ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myRadioButtons", "nonExistingOption" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueException.class, content.getData( "myRadioButtons" ) );
    }

    @Test
    public void given_illegal_type_for_longitude_when_checkValidity_then_InvalidDataException_is_thrown()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myGeoLocation" ).type( ComponentTypes.GEO_LOCATION ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myGeoLocation.latitude", 0.0 );
        content.setData( "myGeoLocation.longitude", "0.0" );

        // exercise
        Validator validator = newValidator().contentType( contentType ).build();
        validateAndAssertInvalidDataException( validator, content, InvalidValueTypeException.class, content.getData( "myGeoLocation" ) );
    }

    @Test
    public void given_illegal_value_for_a_dataSet_defined_by_a_dataType_when_checkValidity_then_InvalidDataException_is_thrown()
    {
        // setup:
        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "myGeoLocation" ).type( ComponentTypes.GEO_LOCATION ).build() );

        Content content = new Content();
        content.setType( contentType );
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
        Content content = new Content();
        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );
        content.setData( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.setData( "crimes[0].year", "1989" );
        content.setData( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.setData( "crimes[1].year", "1990" );

        assertEquals( DataTypes.TEXT, content.getData( "personalia.eyeColour" ).getDataType() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData( "personalia.eyeColour" ).getPath().toString() );

        // exercise
        ContentType contentType = new ContentType();
        contentType.addFormItem( newComponent().name( "name" ).type( ComponentTypes.TEXT_LINE ).build() );
        FormItemSet personalia = newFormItemSet().name( "personalia" ).multiple( false ).build();
        contentType.addFormItem( personalia );
        personalia.addFormItem( newComponent().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        personalia.addFormItem( newComponent().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        FormItemSet crimes = newFormItemSet().name( "crimes" ).multiple( true ).build();
        contentType.addFormItem( crimes );
        crimes.addItem( newComponent().name( "description" ).type( ComponentTypes.TEXT_LINE ).build() );
        crimes.addItem( newComponent().name( "year" ).type( ComponentTypes.TEXT_LINE ).build() );
        content.setType( contentType );

        assertEquals( DataTypes.TEXT, content.getData( "personalia.eyeColour" ).getDataType() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );

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
            assertTrue( "Expected exception" + InvalidDataException.class.getSimpleName(), InvalidDataException.class.isInstance( e ) );
            assertTrue( "Expected cause" + cause.getSimpleName(), cause.isInstance( e.getCause() ) );
            InvalidDataException invalidDataException = (InvalidDataException) e;
            assertEquals( data.getPath(), invalidDataException.getData().getPath() );
        }
    }
}
