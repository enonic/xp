package com.enonic.xp.core.impl.content.validate;


import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class OccurrenceValidatorTest
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

    private OccurrenceValidator newValidator( final ContentType type )
    {
        return new OccurrenceValidator( type.getForm() );
    }

    @Test
    public void given_input_with_maxOccur1_with_two_data_when_validate_then_MaximumOccurrencesValidationError()
    {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "1" );
        content.getData().setString( "myInput[1]", "2" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MaximumOccurrencesValidationError );
        assertEquals( "Input [myInput] allows maximum 1 occurrence: 2", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_input_with_maxOccur2_with_three_data_when_validate_then_MaximumOccurrencesValidationError()
    {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 2 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "1" );
        content.getData().setString( "myInput[1]", "2" );
        content.getData().setString( "myInput[2]", "3" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MaximumOccurrencesValidationError );
        assertEquals( "Input [myInput] allows maximum 2 occurrences: 3", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_required_input_with_data_when_validate_then_hasErrors_returns_false()
    {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput", "value" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_input_with_no_data_when_validate_then_hasErrors_returns_true()
    {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
    }

    @Test
    public void given_input_with_minOccur2_with_one_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 2 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput", "value" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
        assertEquals( "Input [myInput] requires minimum 2 occurrences: 1", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_input_with_minOccur3_with_two_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 3 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "value 1" );
        content.getData().setString( "myInput[1]", "value 2" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
        assertEquals( "Input [myInput] requires minimum 3 occurrences: 2", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_required_field_with_no_data_within_layout_when_validate_then_MinimumOccurrencesValidationError()
    {

        contentType.getForm().addFormItem( FieldSet.create().label( "My layout" ).name( "myLayout" ).addFormItem(
            Input.create().name( "myField" ).label( "Field" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).build();

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_required_input_with_no_data_within_layout_within_layout_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.getForm().addFormItem( FieldSet.create().label( "My outer layout" ).name( "myOuterlayout" ).addFormItem(
            FieldSet.create().label( "My Layout" ).name( "myLayout" ).addFormItem(
                Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required(
                    true ).build() ).build() ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test()
    public void given_required_set_with_data_when_validate_then_hasErrors_returns_false()
    {
        contentType.getForm().addFormItem( FormItemSet.create().name( "mySet" ).required( true ).addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "mySet.myInput", "value" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_set_with_no_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.getForm().addFormItem( FormItemSet.create().name( "mySet" ).required( true ).addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
        assertEquals( "FormItemSet [mySet] requires minimum 1 occurrence: 0", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_required_set_with_no_data_within_layout_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.getForm().addFormItem( FieldSet.create().label( "My layout" ).name( "myLayout" ).addFormItem(
            FormItemSet.create().name( "mySet" ).required( true ).addFormItem(
                Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_required_input_at_top_and_inside_formItemSet_and_formItemSet_have_other_unrequired_data_when_validate_then_two_errors_are_found()
    {
        Input myInput =
            Input.create().name( "myRequiredInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build();
        FormItemSet mySet = FormItemSet.create().name( "mySet" ).required( false ).addFormItem( myInput ).build();
        contentType.getForm().addFormItem( mySet );
        contentType.getForm().addFormItem(
            Input.create().name( "myOtherRequiredInput" ).label( "Other input" ).inputType( InputTypeName.TEXT_LINE ).required(
                true ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "mySet.myUnrequiredData", "1" );

        assertEquals( "mySet.myRequiredInput", mySet.getInput( "myRequiredInput" ).getPath().toString() );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertEquals( 2, validationResults.size() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );

        Iterator<ValidationError> dataValidationErrorIterator = validationResults.iterator();

        DataValidationError nextDataValidationError = (DataValidationError) dataValidationErrorIterator.next();
        assertTrue( nextDataValidationError instanceof MinimumOccurrencesValidationError );
        assertEquals( "Input [mySet.myRequiredInput] requires minimum 1 occurrence: 0", nextDataValidationError.getErrorMessage() );
        assertNotNull( nextDataValidationError.getPath() );
        nextDataValidationError = (DataValidationError) dataValidationErrorIterator.next();
        assertTrue( nextDataValidationError instanceof MinimumOccurrencesValidationError );
        assertEquals( "Input [myOtherRequiredInput] requires minimum 1 occurrence: 0", nextDataValidationError.getErrorMessage() );
    }

    @Test
    public void data_for_input_is_not_required_if_parent_data_set_does_not_exist()
    {
        Input myInput =
            Input.create().name( "myRequiredInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build();
        FormItemSet mySet = FormItemSet.create().name( "mySet" ).required( false ).addFormItem( myInput ).build();
        contentType.getForm().addFormItem( mySet );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myData", "1" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
        assertEquals( 0, validationResults.size() );
    }

    @Test
    public void given_required_set_with_no_data_and_other_set_with_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        // setup
        contentType.getForm().addFormItem( Input.create().name( "name" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() );

        FormItemSet personalia = FormItemSet.create().name( "personalia" ).multiple( false ).required( true ).build();
        personalia.add( Input.create().name( "eyeColour" ).label( "Eye color" ).inputType( InputTypeName.TEXT_LINE ).build() );
        personalia.add( Input.create().name( "hairColour" ).label( "Hair color" ).inputType( InputTypeName.TEXT_LINE ).build() );
        contentType.getForm().addFormItem( personalia );

        FormItemSet crimes = FormItemSet.create().name( "crimes" ).multiple( true ).build();
        contentType.getForm().addFormItem( crimes );
        crimes.add( Input.create().name( "description" ).label( "Description" ).inputType( InputTypeName.TEXT_LINE ).build() );
        crimes.add( Input.create().name( "year" ).label( "Year" ).inputType( InputTypeName.TEXT_LINE ).build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        content.getData().setString( "name", "Thomas" );
        content.getData().setString( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.getData().setString( "crimes[0].year", "1989" );
        content.getData().setString( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.getData().setString( "crimes[1].year", "1990" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
        assertEquals( "FormItemSet [personalia] requires minimum 1 occurrence: 0", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_input_with_maxOccur1_with_two_nonnull_data_when_validate_then_MaximumOccurrencesValidationError() {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "1" );
        content.getData().setString( "myInput[1]", "2" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
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
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
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
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
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
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
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
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
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
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_input_with_minOccur1_and_maxOccur2_with_one_null_value_when_validate_then_hasErrors_returns_MinimumOccurrencesValidationError() {
        contentType.getForm().addFormItem(
            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", null );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
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
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
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
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_formoptionset_with_input_with_minOccur1_with_one_null_value_when_validate_then_hasErrors_returns_MinimumOccurrencesValidationError()
    {
        FormOptionSet.Builder crimes = FormOptionSet.create().
            occurrences( Occurrences.create( 1, 0 ) ).
            name( "crimes" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().
            name( "option1" ).
            defaultOption( true );

        option1.addFormItem( Input.create().name( "description" ).label( "Description" ).minimumOccurrences( 1 ).inputType(
            InputTypeName.TEXT_LINE ).build() );
        option1.addFormItem( Input.create().name( "year" ).label( "Year" ).inputType( InputTypeName.TEXT_LINE ).build() );

        crimes.addOptionSetOption( option1.build() );
        contentType.getForm().addFormItem( crimes.build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "crimes[0].option1.description", null );
        content.getData().setString( "crimes[0].option1.year", "1989" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }


    @Test
    public void given_formoptionset_with_two_inputs_with_minOccur_with_data_when_validate_then_hasErrors_returns_false()
    {
        FormOptionSet.Builder crimes = FormOptionSet.create().
            occurrences( Occurrences.create( 1, 0 ) ).
            name( "crimes" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().
            name( "option1" ).
            defaultOption( true );

        option1.addFormItem( Input.create().name( "description" ).label( "Description" ).minimumOccurrences( 1 ).inputType(
            InputTypeName.TEXT_LINE ).build() );
        option1.addFormItem( Input.create().name( "year" ).label( "Year" ).minimumOccurrences( 2 ).maximumOccurrences( 0 ).inputType(
            InputTypeName.TEXT_LINE ).build() );

        crimes.addOptionSetOption( option1.build() );
        contentType.getForm().addFormItem( crimes.build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "crimes[0].option1.description", "descr" );
        content.getData().setString( "crimes[0].option1.year[0]", "1989" );
        content.getData().setString( "crimes[0].option1.year[1]", "1990" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test()
    public void given_required_optionset_with_data_when_validate_then_hasErrors_returns_false()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().
            occurrences( Occurrences.create( 1, 0 ) ).
            name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().
            name( "option1" ).
            defaultOption( true );

        option1.addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() );

        myOptionSet.addOptionSetOption( option1.build() );
        contentType.getForm().addFormItem( myOptionSet.build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myInput", "value" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_optionset_with_no_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().
            occurrences( Occurrences.create( 1, 0 ) ).
            name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().
            name( "option1" ).
            defaultOption( true );

        option1.addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() );

        myOptionSet.addOptionSetOption( option1.build() );
        contentType.getForm().addFormItem( myOptionSet.build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
        assertEquals( "FormOptionSet [myOptionSet] requires minimum 1 occurrence: 0", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_required_optionset_with_no_data_within_layout_when_validate_then_MinimumOccurrencesValidationError()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().
            occurrences( Occurrences.create( 1, 0 ) ).
            name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().
            name( "option1" ).
            defaultOption( true );

        option1.addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() );

        myOptionSet.addOptionSetOption( option1.build() );

        contentType.getForm().addFormItem(
            FieldSet.create().label( "My layout" ).name( "myLayout" ).addFormItem( myOptionSet.build() ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
    }

    @Test
    public void given_required_input_at_top_and_inside_formOptionSet_and_formOptionSet_have_other_unrequired_data_when_validate_then_two_errors_are_found()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().
            occurrences( Occurrences.create( 1, 0 ) ).
            name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().
            name( "option1" ).
            defaultOption( true );

        option1.addFormItem(
            Input.create().name( "myRequiredInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() );

        myOptionSet.addOptionSetOption( option1.build() );

        contentType.getForm().addFormItem( myOptionSet.build() );

        contentType.getForm().addFormItem(
            Input.create().name( "myOtherRequiredInput" ).label( "Other input" ).inputType( InputTypeName.TEXT_LINE ).required(
                true ).build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );

        //assertEquals( "myOptionSet.option1.myRequiredInput", myOptionSet.getInput( "myRequiredInput" ).getPath().toString() );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertEquals( 2, validationResults.size() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );

        Iterator<ValidationError> dataValidationErrorIterator = validationResults.iterator();

        DataValidationError nextDataValidationError = (DataValidationError) dataValidationErrorIterator.next();
        assertTrue( nextDataValidationError instanceof MinimumOccurrencesValidationError );
        assertEquals( "Input [myOptionSet.option1.myRequiredInput] requires minimum 1 occurrence: 0",
                      nextDataValidationError.getErrorMessage() );
        assertNotNull( nextDataValidationError.getPath() );
        nextDataValidationError = (DataValidationError) dataValidationErrorIterator.next();
        assertTrue( nextDataValidationError instanceof MinimumOccurrencesValidationError );
        assertEquals( "Input [myOtherRequiredInput] requires minimum 1 occurrence: 0", nextDataValidationError.getErrorMessage() );
    }


    @Test
    public void data_for_input_is_not_required_if_parent_optionset_does_not_exist()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().
            required( false ).
            name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().
            name( "option1" ).
            defaultOption( true );

        option1.addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() );

        myOptionSet.addOptionSetOption( option1.build() );

        contentType.getForm().addFormItem( myOptionSet.build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myData", "1" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
        assertEquals( 0, validationResults.size() );
    }

    @Test
    public void given_required_optionset_with_no_data_and_other_optionset_with_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        FormOptionSet.Builder personalia = FormOptionSet.create().
            multiple( false ).
            required( true ).
            name( "personalia" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().
            name( "option1" ).
            defaultOption( true );

        option1.addFormItem( Input.create().name( "eyeColour" ).label( "Eye color" ).inputType( InputTypeName.TEXT_LINE ).build() );
        option1.addFormItem( Input.create().name( "hairColour" ).label( "Hair color" ).inputType( InputTypeName.TEXT_LINE ).build() );

        personalia.addOptionSetOption( option1.build() );
        contentType.getForm().addFormItem( personalia.build() );

        FormOptionSet.Builder crimes = FormOptionSet.create().
            multiple( false ).
            name( "crimes" );

        FormOptionSetOption.Builder crimesOption1 = FormOptionSetOption.create().
            name( "option1" ).
            defaultOption( true );

        crimesOption1.addFormItem(
            Input.create().name( "description" ).label( "Description" ).inputType( InputTypeName.TEXT_LINE ).build() );
        crimesOption1.addFormItem( Input.create().name( "year" ).label( "Year" ).inputType( InputTypeName.TEXT_LINE ).build() );

        personalia.addOptionSetOption( crimesOption1.build() );
        contentType.getForm().addFormItem( crimes.build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        content.getData().setString( "name", "Thomas" );
        content.getData().setString( "crimes[0].option1.description", "Stole tomatoes from neighbour" );
        content.getData().setString( "crimes[0].option1..year", "1989" );
        content.getData().setString( "crimes[1].option1..description", "Stole a chocolate from the Matbua shop" );
        content.getData().setString( "crimes[1].option1..year", "1990" );

        // exercise
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof MinimumOccurrencesValidationError );
        assertEquals( "FormOptionSet [personalia] requires minimum 1 occurrence: 0", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_optionset_with_default_selection_passes_multiselection_check()
    {
        contentType.getForm().addFormItem( makeOptionSet( "myOptionSet", 1, 1, 1, 1 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_optionset_with_required_selection_and_empty_selection_array_fails_multiselection_check()
    {
        contentType.getForm().addFormItem( makeOptionSet( "myOptionSet", 0, 0, 1, 1 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );
        content.getData().setString( "myOptionSet._selected", "1" );
        content.getData().removeProperty( "myOptionSet._selected" );
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof OptionSetSelectionValidationError );
        assertEquals( "OptionSet [myOptionSet] requires min 1 max 1 items selected: 0", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_optionset_with_required_selection_selection_array_hast_too_many_selected_options()
    {
        contentType.getForm().addFormItem( makeOptionSet( "myOptionSet", 3, 0, 1, 2 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );
        content.getData().getSet( "myOptionSet" ).addString( "_selected", "option1" );
        content.getData().getSet( "myOptionSet" ).addString( "_selected", "option2" );
        content.getData().getSet( "myOptionSet" ).addString( "_selected", "option3" );

        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof OptionSetSelectionValidationError );
        assertEquals( "OptionSet [myOptionSet] requires min 1 max 2 items selected: 3", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_optionset_with_required_selection_selection_array_hast_too_little_selected_options()
    {
        contentType.getForm().addFormItem( makeOptionSet( "myOptionSet", 3, 0, 3, 3 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );
        content.getData().getSet( "myOptionSet" ).addString( "_selected", "option1" );
        content.getData().getSet( "myOptionSet" ).addString( "_selected", "option2" );

        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof OptionSetSelectionValidationError );
        assertEquals( "OptionSet [myOptionSet] requires min 3 max 3 items selected: 2", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_optionset_with_required_selection_and_missing_selection_array_has_too_many_default_options()
    {
        contentType.getForm().addFormItem( makeOptionSet( "myOptionSet", 0, 2, 1, 1 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );
        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof OptionSetSelectionValidationError );
        assertEquals( "OptionSet [myOptionSet] requires min 1 max 1 items selected: 2", validationResults.getFirst().getErrorMessage() );
    }

    @Test
    public void given_optionset_with_required_selection_and_missing_selection_array_has_too_little_default_options()
    {
        contentType.getForm().addFormItem( makeOptionSet( "myOptionSet", 2, 0, 1, 1 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );

        ValidationErrors validationResults = newValidator( contentType ).validate( content.getData().getRoot() );
        assertTrue( validationResults.hasErrors() );
        assertTrue( validationResults.getFirst() instanceof OptionSetSelectionValidationError );
        assertEquals( "OptionSet [myOptionSet] requires min 1 max 1 items selected: 0", validationResults.getFirst().getErrorMessage() );
    }

    private FormOptionSet makeOptionSet( final String name, int numberOfOptions, int numberOfDefaultOptions, int minSelection,
                                         int maxSelection )
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().
            required( false ).
            multiselection( Occurrences.create( minSelection, maxSelection ) ).
            name( name );

        int optionsCounter = 1;

        for ( int i = 1; i <= numberOfOptions; i++, optionsCounter++ )
        {
            FormOptionSetOption.Builder option = FormOptionSetOption.create().name( "option" + optionsCounter );
            myOptionSet.addOptionSetOption( option.build() );
        }

        for ( int i = 1; i <= numberOfDefaultOptions; i++, optionsCounter++ )
        {
            FormOptionSetOption.Builder option = FormOptionSetOption.create().name( "option" + optionsCounter ).defaultOption( true );
            myOptionSet.addOptionSetOption( option.build() );
        }

        return myOptionSet.build();
    }
}
