package com.enonic.wem.api.content.schema.content.validator;


import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.form.FieldSet;
import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
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
    public void given_input_with_maxOccur1_with_two_data_when_validate_then_MaximumOccurrencesValidationError()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "myInput[0]", new Value.Text( "1" ) );
        content.getRootDataSet().setData( "myInput[1]", new Value.Text( "2" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MaximumOccurrencesValidationError );
        assertEquals( "Input [myInput] allows maximum 1 occurrence: 2", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_input_with_maxOccur2_with_three_data_when_validate_then_MaximumOccurrencesValidationError()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).maximumOccurrences( 2 ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "myInput[0]", new Value.Text( "1" ) );
        content.getRootDataSet().setData( "myInput[1]", new Value.Text( "2" ) );
        content.getRootDataSet().setData( "myInput[2]", new Value.Text( "3" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MaximumOccurrencesValidationError );
        assertEquals( "Input [myInput] allows maximum 2 occurrences: 3", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_required_input_with_data_when_validate_then_hasErrors_returns_false()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).required( true ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "myInput", new Value.Text( "value" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_input_with_no_data_when_validate_then_hasErrors_returns_true()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).required( true ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
    }

    @Test
    public void given_input_with_minOccur1_with_one_data_with_blank_value_when_validate_then_hasErrors_returns_true()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "myInput", new Value.Text( "" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertEquals( "Missing required value for input [myInput] of type [TextLine]: ", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_input_with_minOccur2_with_one_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).minimumOccurrences( 2 ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "myInput", new Value.Text( "value" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
        assertEquals( "Input [myInput] requires minimum 2 occurrences: 1", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_input_with_minOccur3_with_two_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).minimumOccurrences( 3 ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "myInput[0]", new Value.Text( "value 1" ) );
        content.getRootDataSet().setData( "myInput[1]", new Value.Text( "value 2" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
        assertEquals( "Input [myInput] requires minimum 3 occurrences: 2", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_input_with_minOccur2_with_two_data_but_one_is_emty_string_when_validate_then_MissingRequiredValueValidationError()
    {
        contentType.form().addFormItem( newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).minimumOccurrences( 2 ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "myInput[0]", new Value.Text( "value 1" ) );
        content.getRootDataSet().setData( "myInput[1]", new Value.Text( "" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MissingRequiredValueValidationError );
        assertEquals( "Missing required value for input [myInput] of type [TextLine]: ", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_required_field_with_no_data_within_layout_when_validate_then_MinimumOccurrencesValidationError()
    {

        contentType.form().addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).add(
            newInput().name( "myField" ).inputType( InputTypes.TEXT_LINE ).required( true ).build() ).build() );
        Content content = newContent().build();

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_required_input_with_no_data_within_layout_within_layout_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.form().addFormItem( newFieldSet().label( "My outer layout" ).name( "myOuterlayout" ).add(
            newFieldSet().label( "My Layout" ).name( "myLayout" ).add(
                newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_required_field_with_empty_string_within_set_within_layout_when_validate_then_MissingRequiredValueValidationError()
    {
        Input myInput = newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).required( true ).build();
        FormItemSet mySet = newFormItemSet().name( "mySet" ).addFormItem( myInput ).build();
        FieldSet myLayout = newFieldSet().label( "My layout" ).name( "myLayout" ).add( mySet ).build();
        contentType.form().addFormItem( myLayout );
        Content content = newContent().build();
        content.getRootDataSet().setData( "mySet.myInput", new Value.Text( "" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MissingRequiredValueValidationError );
    }

    @Test
    public void given_required_input_with_empty_string_within_set_when_validate_then_MissingRequiredValueValidationError()
    {
        Input myInput = newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).required( true ).build();
        FormItemSet mySet = newFormItemSet().name( "mySet" ).required( true ).addFormItem( myInput ).build();
        contentType.form().addFormItem( mySet );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "mySet.myInput", new Value.Text( "" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MissingRequiredValueValidationError );
    }

    @Test
    public void given_required_field_with_empty_string_within_layout_within_a_set_when_validate_then_MissingRequiredValueValidationError()
    {
        Input myRequiredInput = newInput().name( "myRequiredInput" ).inputType( InputTypes.TEXT_LINE ).required( true ).build();
        FieldSet myLayout = newFieldSet().label( "My layout" ).name( "myLayout" ).add( myRequiredInput ).build();
        FormItemSet myRequiredSet = newFormItemSet().name( "mySet" ).required( false ).addFormItem( myLayout ).build();

        contentType.form().addFormItem( myRequiredSet );
        Content content = newContent().build();
        content.getRootDataSet().setData( "mySet.myRequiredInput", new Value.Text( "" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );

        // verify
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MissingRequiredValueValidationError );
    }

    @Test()
    public void given_required_set_with_data_when_validate_then_hasErrors_returns_false()
    {
        contentType.form().addFormItem( newFormItemSet().name( "mySet" ).required( true ).addFormItem(
            newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).build() ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "mySet.myInput", new Value.Text( "value" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_set_with_no_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.form().addFormItem( newFormItemSet().name( "mySet" ).required( true ).addFormItem(
            newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).build() ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
        assertEquals( "FormItemSet [mySet] requires minimum 1 occurrence: 0", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_required_set_with_no_data_within_layout_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.form().addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).add(
            newFormItemSet().name( "mySet" ).required( true ).addFormItem(
                newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_required_input_at_top_and_inside_formItemSet_and_formItemSet_have_other_unrequired_data_when_validate_then_two_errors_are_found()
    {
        Input myInput = newInput().name( "myRequiredInput" ).inputType( InputTypes.TEXT_LINE ).required( true ).build();
        FormItemSet mySet = newFormItemSet().name( "mySet" ).required( false ).addFormItem( myInput ).build();
        contentType.form().addFormItem( mySet );
        contentType.form().addFormItem(
            newInput().name( "myOtherRequiredInput" ).inputType( InputTypes.TEXT_LINE ).required( true ).build() );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "mySet.myUnrequiredData", new Value.Text( "1" ) );

        assertEquals( "mySet.myRequiredInput", mySet.getInput( "myRequiredInput" ).getPath().toString() );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertEquals( 2, validationResults.size() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );

        Iterator<DataValidationError> dataValidationErrorIterator = validationResults.iterator();

        DataValidationError nextDataValidationError = dataValidationErrorIterator.next();
        assertTrue( nextDataValidationError instanceof MinimumOccurrencesValidationError );
        assertEquals( "Input [mySet.myRequiredInput] requires minimum 1 occurrence: 0", nextDataValidationError.getErrorMessage() );

        nextDataValidationError = dataValidationErrorIterator.next();
        assertTrue( nextDataValidationError instanceof MinimumOccurrencesValidationError );
        assertEquals( "Input [myOtherRequiredInput] requires minimum 1 occurrence: 0", nextDataValidationError.getErrorMessage() );
    }

    @Test
    public void data_for_input_is_not_required_if_parent_data_set_does_not_exist()
    {
        Input myInput = newInput().name( "myRequiredInput" ).inputType( InputTypes.TEXT_LINE ).required( true ).build();
        FormItemSet mySet = newFormItemSet().name( "mySet" ).required( false ).addFormItem( myInput ).build();
        contentType.form().addFormItem( mySet );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "myData", new Value.Text( "1" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertFalse( validationResults.hasErrors() );
        assertEquals( 0, validationResults.size() );
    }

    @Test
    public void todo_required_data_not_given_in_second_instance_of_unrequired_set()
    {
        Input myInput = newInput().name( "myRequiredInput" ).inputType( InputTypes.TEXT_LINE ).required( true ).build();
        FormItemSet mySet = newFormItemSet().name( "mySet" ).required( false ).multiple( true ).addFormItem( myInput ).build();
        contentType.form().addFormItem( mySet );
        Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getRootDataSet().setData( "mySet[0].myRequiredInput", new Value.Text( "1" ) );
        content.getRootDataSet().setData( "mySet[1].myRequiredInput", new Value.Text( "" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertEquals( 1, validationResults.size() );
    }

    @Test
    public void given_required_set_with_no_data_and_other_set_with_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        // setup
        contentType.form().addFormItem( newInput().name( "name" ).inputType( InputTypes.TEXT_LINE ).build() );

        FormItemSet personalia = newFormItemSet().name( "personalia" ).multiple( false ).required( true ).build();
        personalia.add( newInput().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() );
        personalia.add( newInput().name( "hairColour" ).inputType( InputTypes.TEXT_LINE ).build() );
        contentType.form().addFormItem( personalia );

        FormItemSet crimes = newFormItemSet().name( "crimes" ).multiple( true ).build();
        contentType.form().addFormItem( crimes );
        crimes.add( newInput().name( "description" ).inputType( InputTypes.TEXT_LINE ).build() );
        crimes.add( newInput().name( "year" ).inputType( InputTypes.TEXT_LINE ).build() );

        Content content = newContent().type( contentType.getQualifiedName() ).build();

        content.getRootDataSet().setData( "name", new Value.Text( "Thomas" ) );
        content.getRootDataSet().setData( "crimes[0].description", new Value.Text( "Stole tomatoes from neighbour" ) );
        content.getRootDataSet().setData( "crimes[0].year", new Value.Text( "1989" ) );
        content.getRootDataSet().setData( "crimes[1].description", new Value.Text( "Stole a chocolate from the Matbua shop" ) );
        content.getRootDataSet().setData( "crimes[1].year", new Value.Text( "1990" ) );

        // exercise
        DataValidationErrors validationResults = new OccurrenceValidator( contentType ).validate( content.getRootDataSet() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
        assertEquals( "FormItemSet [personalia] requires minimum 1 occurrence: 0", validationResults.getFirst().getErrorMessage() );
    }

}
