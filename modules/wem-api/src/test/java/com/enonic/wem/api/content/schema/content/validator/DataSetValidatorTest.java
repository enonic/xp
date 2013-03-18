package com.enonic.wem.api.content.schema.content.validator;

import org.joda.time.DateMidnight;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.type.DataTypes;
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
            newInput().name( "mySingleSelector" ).inputType( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "mySingleSelector", "nonExistingOption" );

        // exercise & verify
        DataSetValidator validator = new DataSetValidator( contentType );
        DataValidationErrors validationErrors = validator.validate( content.getRootDataSet() );
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
        content.getRootDataSet().setData( "mySingleSelector1", "nonExistingOption" );
        content.getRootDataSet().setData( "mySingleSelector2", "nonExistingOption" );

        // exercise & verify
        DataSetValidator validator = new DataSetValidator( contentType );
        DataValidationErrors validationErrors = validator.validate( content.getRootDataSet() );
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
        content.getRootDataSet().setData( "myDate", new DateMidnight( 2012, 9, 11 ) );
        content.getRootDataSet().setData( "myDecimalNumber", 12.34 );
        content.getRootDataSet().setData( "mySingleSelector", "o1" );
        content.getRootDataSet().setData( "myHtmlArea", "<h1>Hello world</h1>" );

        // exercise
        DataSetValidator validator = new DataSetValidator( contentType );
        DataValidationErrors validationErrors = validator.validate( content.getRootDataSet() );
        assertFalse( validationErrors.hasErrors() );
    }

    @Test
    public void given_untyped_content_when_setting_type_that_fits_then_everything_validates()
    {
        // setup
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setData( "name", "Thomas" );
        rootDataSet.setData( "personalia.eyeColour", "Blue" );
        rootDataSet.setData( "personalia.hairColour", "Blonde" );
        rootDataSet.setData( "crimes[0].description", "Stole tomatoes from neighbour" );
        rootDataSet.setData( "crimes[0].year", "1989" );
        rootDataSet.setData( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        rootDataSet.setData( "crimes[1].year", "1990" );

        assertEquals( DataTypes.TEXT, rootDataSet.getData( "personalia.eyeColour" ).getType() );
        Assert.assertEquals( "Blue", rootDataSet.getData( "personalia.eyeColour" ).getObject() );
        Assert.assertEquals( "personalia.eyeColour", rootDataSet.getData( "personalia.eyeColour" ).getPath().toString() );

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

        assertEquals( DataTypes.TEXT, rootDataSet.getData( "personalia.eyeColour" ).getType() );
        Assert.assertEquals( "Blue", rootDataSet.getData( "personalia.eyeColour" ).getObject() );

        // verify
        DataSetValidator validator = new DataSetValidator( contentType );
        DataValidationErrors validationErrors = validator.validate( rootDataSet );
        assertFalse( validationErrors.hasErrors() );
    }

}
