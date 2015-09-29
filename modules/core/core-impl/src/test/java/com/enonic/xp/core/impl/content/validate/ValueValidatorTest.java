package com.enonic.xp.core.impl.content.validate;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ValueValidatorTest
{
    private static final ContentPath MY_CONTENT_PATH = ContentPath.from( "/mycontent" );

    private ContentType contentType;

    @Before
    public void before()
    {
        contentType = ContentType.create().
            name( "myapplication:my_type" ).
            superType( ContentTypeName.structured() ).
            build();
    }

    private ValueValidator newValidator( final ContentType type )
    {
        return new ValueValidator( type.getForm() );
    }

    @Test
    public void given_input_with_maxOccur1_with_two_nonnull_data_when_validate_then_MaximumOccurrencesValidationError() {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "1" );
        content.getData().setString( "myInput[1]", "2" );

        // exercise
        DataValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MaximumOccurrencesValidationError );
    }

    @Test
    public void given_input_with_maxOccur1_with_three_null_data_when_validate_then_hasErrors_returns_false() {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", null );
        content.getData().setString( "myInput[1]", null );
        content.getData().setString( "myInput[2]", null );

        // exercise
        DataValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_input_with_maxOccur1_with_one_null_and_one_nonnull_value_when_validate_then_hasErrors_returns_false() {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "1" );
        content.getData().setString( "myInput[1]", null );

        // exercise
        DataValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_input_with_minOccur1_with_two_null_values_when_validate_then_hasErrors_returns_MinimumOccurrencesValidationError() {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", null );
        content.getData().setString( "myInput[1]", null );

        // exercise
        DataValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_input_with_minOccur1_with_one_null_value_when_validate_then_hasErrors_returns_MinimumOccurrencesValidationError() {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", null );

        // exercise
        DataValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_input_with_minOccur1_with_two_nonnull_values_when_validate_then_hasErrors_returns_false() {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "1" );
        content.getData().setString( "myInput[1]", null );

        // exercise
        DataValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_input_with_minOccur1_and_maxOccur2_with_one_null_value_when_validate_then_hasErrors_returns_MinimumOccurrencesValidationError() {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", null );

        // exercise
        DataValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_formitemset_with_input_with_minOccur1_with_one_null_value_when_validate_then_hasErrors_returns_MinimumOccurrencesValidationError() {
        FormItemSet crimes = FormItemSet.create().name( "crimes" ).multiple( true ).build();
        contentType.getForm().addFormItem( crimes );
        crimes.add( Input.create().name( "description" ).label( "Description" ).minimumOccurrences( 1 ).inputType( InputTypeName.TEXT_LINE ).build() );
        crimes.add( Input.create().name( "year" ).label( "Year" ).inputType( InputTypeName.TEXT_LINE ).build() );


        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "crimes[0].description", null );
        content.getData().setString( "crimes[0].year", "1989" );

        // exercise
        DataValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_formitemset_with_two_inputs_with_minOccur_with_data_when_validate_then_hasErrors_returns_false() {
        FormItemSet crimes = FormItemSet.create().name( "crimes" ).multiple( true ).build();
        contentType.getForm().addFormItem( crimes );
        crimes.add( Input.create().name( "description" ).label( "Description" ).minimumOccurrences( 1 ).inputType( InputTypeName.TEXT_LINE ).build() );
        crimes.add( Input.create().name( "year" ).label( "Year" ).minimumOccurrences( 2 ).maximumOccurrences( 0 ).inputType( InputTypeName.TEXT_LINE ).build() );


        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "crimes[0].description", "descr" );
        content.getData().setString( "crimes[0].year[0]", "1989" );
        content.getData().setString( "crimes[0].year[1]", "1990" );

        // exercise
        DataValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
    }

}
