package com.enonic.wem.api.content.type;


import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;

public class OccurrenceValidatorTest
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
    public void given_input_with_maxOccur1_with_two_data_when_validate_then_exception_is_thrown()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myInput[0]", "1" );
        content.setData( "myInput[1]", "2" );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MaximumOccurrencesValidationError );
        assertEquals( "Input [myInput] allows maximum 1 occurrence: 2", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_input_with_maxOccur2_with_three_data_when_validate_then_exception_is_thrown()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).maximumOccurrences( 2 ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myInput[0]", "1" );
        content.setData( "myInput[1]", "2" );
        content.setData( "myInput[2]", "3" );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MaximumOccurrencesValidationError );
        assertEquals( "Input [myInput] allows maximum 2 occurrences: 3", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_required_input_with_data_when_validate_then_exception_is_not_thrown()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myInput", "value" );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_input_with_no_data_when_validate_then_exception_is_thrown()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
    }

    @Test
    public void given_input_with_minOccur1_with_one_data_with_blank_value_when_validate_then_exception_is_thrown()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myInput", "" );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertEquals( "Missing required value in input [myInput] of type [TextLine]: ", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_input_with_minOccur2_with_one_data_when_validate_then_exception_is_thrown()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).minimumOccurrences( 2 ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myInput", "value" );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
        assertEquals( "Input [myInput] requires minimum 2 occurrences: 1", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_required_field_with_no_data_within_layout_when_validate_then_exception_is_thrown()
    {

        contentType.form().addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).add(
            newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() );
        Content content = new Content();

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_required_input_with_no_data_within_layout_within_layout_when_validate_then_exception_is_thrown()
    {
        contentType.form().addFormItem( newFieldSet().label( "My outer layout" ).name( "myOuterlayout" ).add(
            newFieldSet().label( "My Layout" ).name( "myLayout" ).add(
                newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_required_field_with_no_data_within_set_within_layout_when_validate_then_exception_is_thrown()
    {
        contentType.form().addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).add(
            newFormItemSet().name( "mySet" ).required( true ).add(
                newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setData( "mySet.myInput", "" );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_required_input_with_no_data_within_set_when_validate_then_exception_is_thrown()
    {
        contentType.form().addFormItem( newFormItemSet().name( "mySet" ).required( true ).add(
            newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "mySet.myInput", "" );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_required_field_with_no_data_within_layout_within_a_set_when_validate_then_exception_is_thrown()
    {
        contentType.form().addFormItem( newFormItemSet().name( "mySet" ).required( true ).add(
            newFieldSet().label( "My layout" ).name( "myLayout" ).add(
                newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setData( "mySet.myInput", "" );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test()
    public void given_required_set_with_data_when_validate_then_exception_is_not_thrown()
    {
        contentType.form().addFormItem( newFormItemSet().name( "mySet" ).required( true ).add(
            newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "mySet.myInput", "value" );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_set_with_no_data_when_validate_then_exception_is_thrown()
    {
        contentType.form().addFormItem( newFormItemSet().name( "mySet" ).required( true ).add(
            newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MissingRequiredValueValidationError );
    }

    @Test
    public void given_required_set_with_no_data_within_layout_when_validate_then_exception_is_thrown()
    {
        contentType.form().addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).add(
            newFormItemSet().name( "mySet" ).required( true ).add(
                newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MissingRequiredValueValidationError );
    }

    @Test
    public void given_required_set_with_no_data_and_other_set_with_data_when_validate_then_exception_is_thrown()
    {
        // setup
        contentType.form().addFormItem( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );

        FormItemSet personalia = newFormItemSet().name( "personalia" ).multiple( false ).required( true ).build();
        personalia.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        personalia.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );
        contentType.form().addFormItem( personalia );

        FormItemSet crimes = newFormItemSet().name( "crimes" ).multiple( true ).build();
        contentType.form().addFormItem( crimes );
        crimes.add( newInput().name( "description" ).type( InputTypes.TEXT_LINE ).build() );
        crimes.add( newInput().name( "year" ).type( InputTypes.TEXT_LINE ).build() );

        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        content.setData( "name", "Thomas" );
        content.setData( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.setData( "crimes[0].year", "1989" );
        content.setData( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.setData( "crimes[1].year", "1990" );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MissingRequiredValueValidationError );
        assertEquals( "Missing required value in FormItemSet [personalia]", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_recordExceptions_is_true_and_invalid_data_when_validate_then_exception_is_recorded()
    {
        contentType.form().addFormItem( newInput().minimumOccurrences( 1 ).name( "minimumOne" ).type( InputTypes.TEXT_LINE ).build() );
        contentType.form().addFormItem( newInput().maximumOccurrences( 2 ).name( "maximumTwo" ).type( InputTypes.TEXT_LINE ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "maximumTwo[0]", "1" );
        content.setData( "maximumTwo[1]", "2" );
        content.setData( "maximumTwo[2]", "3" );

        // exercise
        final DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getData() );

        // verify
        assertTrue( "No exceptions recorded", validationResults.hasErrors() );
        assertEquals( "Two exceptions expected", 2, validationResults.getSize() );
    }

}
