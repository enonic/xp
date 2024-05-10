package com.enonic.xp.core.impl.content.validate;


import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DataValidationError;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OccurrenceValidatorTest
{
    private static final ContentPath MY_CONTENT_PATH = ContentPath.from( "/mycontent" );

    private ContentType contentType;

    @BeforeEach
    public void before()
    {
        contentType = ContentType.create().name( "myapplication:my_type" ).superType( ContentTypeName.structured() ).build();
    }

    private ValidationErrors validate( final Content content )
    {
        ValidationErrors.Builder validationResultsBuilder = ValidationErrors.create();
        OccurrenceValidator.validate( contentType.getForm(), content.getData().getRoot(), validationResultsBuilder );
        return validationResultsBuilder.build();
    }

    @Test
    public void given_input_with_maxOccur1_with_two_data_when_validate_then_MaximumOccurrencesValidationError()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "1" );
        content.getData().setString( "myInput[1]", "2" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "myInput", 1, 2 );
    }

    @Test
    public void given_input_with_maxOccur2_with_three_data_when_validate_then_MaximumOccurrencesValidationError()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 2 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "1" );
        content.getData().setString( "myInput[1]", "2" );
        content.getData().setString( "myInput[2]", "3" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "myInput", 2, 3 );
    }

    @Test
    public void given_required_input_with_data_when_validate_then_hasErrors_returns_false()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput", "value" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_input_with_no_data_when_validate_then_hasErrors_returns_true()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertTrue( validationResults.hasErrors() );
    }

    @Test
    public void given_required_input_with_no_data_when_validate_then_validation_error_propertyPath_correct()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).get( as( type( DataValidationError.class ) ) )
            .extracting( DataValidationError::getPropertyPath )
            .asString()
            .isEqualTo( "myInput" );
    }

    @Test
    public void given_input_with_minOccur2_with_one_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 2 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput", "value" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "myInput", 2, 1 );
    }

    @Test
    public void given_input_with_minOccur3_with_two_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 3 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "value 1" );
        content.getData().setString( "myInput[1]", "value 2" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "myInput", 3, 2 );
    }

    @Test
    public void given_required_field_with_no_data_within_layout_when_validate_then_MinimumOccurrencesValidationError()
    {

        contentType.getForm()
            .getFormItems()
            .add( FieldSet.create()
                      .label( "My layout" )
                      .name( "myLayout" )
                      .addFormItem(
                          Input.create().name( "myField" ).label( "Field" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() )
                      .build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).build();

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class );
    }

    @Test
    public void given_required_input_with_no_data_within_layout_within_layout_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.getForm()
            .getFormItems()
            .add( FieldSet.create()
                      .label( "My outer layout" )
                      .name( "myOuterlayout" )
                      .addFormItem( FieldSet.create()
                                        .label( "My Layout" )
                                        .name( "myLayout" )
                                        .addFormItem( Input.create()
                                                          .name( "myInput" )
                                                          .label( "Input" )
                                                          .inputType( InputTypeName.TEXT_LINE )
                                                          .required( true )
                                                          .build() )
                                        .build() )
                      .build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class );
    }

    @Test()
    public void given_required_set_with_data_when_validate_then_hasErrors_returns_false()
    {
        contentType.getForm()
            .getFormItems()
            .add( FormItemSet.create()
                      .name( "mySet" )
                      .required( true )
                      .addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() )
                      .build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "mySet.myInput", "value" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_set_with_no_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.getForm()
            .getFormItems()
            .add( FormItemSet.create()
                      .name( "mySet" )
                      .required( true )
                      .addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() )
                      .build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "mySet", 1, 0 );
    }

    @Test
    public void given_required_set_with_no_data_within_layout_when_validate_then_MinimumOccurrencesValidationError()
    {
        contentType.getForm()
            .getFormItems()
            .add( FieldSet.create()
                      .label( "My layout" )
                      .name( "myLayout" )
                      .addFormItem( FormItemSet.create()
                                        .name( "mySet" )
                                        .required( true )
                                        .addFormItem(
                                            Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() )
                                        .build() )
                      .build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class );
    }

    @Test
    public void given_required_input_at_top_and_inside_formItemSet_and_formItemSet_have_other_unrequired_data_when_validate_then_two_errors_are_found()
    {
        Input myInput =
            Input.create().name( "myRequiredInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build();
        FormItemSet mySet = FormItemSet.create().name( "mySet" ).required( false ).addFormItem( myInput ).build();
        contentType.getForm().getFormItems().add( mySet );
        contentType.getForm()
            .getFormItems()
            .add( Input.create()
                      .name( "myOtherRequiredInput" )
                      .label( "Other input" )
                      .inputType( InputTypeName.TEXT_LINE )
                      .required( true )
                      .build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "mySet.myUnrequiredData", "1" );

        assertEquals( "mySet.myRequiredInput", mySet.getInput( "myRequiredInput" ).getPath().toString() );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream() ).hasSize( 2 )
            .allMatch( ve -> ve instanceof DataValidationError )
            .extracting( ValidationError::getArgs )
            .containsExactly( Arrays.array( List.of( "mySet.myRequiredInput", 1, 0 ), List.of( "myOtherRequiredInput", 1, 0 ) ) );
    }

    @Test
    public void data_for_input_is_not_required_if_parent_data_set_does_not_exist()
    {
        Input myInput =
            Input.create().name( "myRequiredInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build();
        FormItemSet mySet = FormItemSet.create().name( "mySet" ).required( false ).addFormItem( myInput ).build();
        contentType.getForm().getFormItems().add( mySet );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myData", "1" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_set_with_no_data_and_other_set_with_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        // setup
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "name" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() );

        FormItemSet personalia = FormItemSet.create().name( "personalia" ).multiple( false ).required( true ).build();
        personalia.add( Input.create().name( "eyeColour" ).label( "Eye color" ).inputType( InputTypeName.TEXT_LINE ).build() );
        personalia.add( Input.create().name( "hairColour" ).label( "Hair color" ).inputType( InputTypeName.TEXT_LINE ).build() );
        contentType.getForm().getFormItems().add( personalia );

        FormItemSet crimes = FormItemSet.create().name( "crimes" ).multiple( true ).build();
        contentType.getForm().getFormItems().add( crimes );
        crimes.add( Input.create().name( "description" ).label( "Description" ).inputType( InputTypeName.TEXT_LINE ).build() );
        crimes.add( Input.create().name( "year" ).label( "Year" ).inputType( InputTypeName.TEXT_LINE ).build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        content.getData().setString( "name", "Thomas" );
        content.getData().setString( "crimes[0].description", "Stole tomatoes from neighbour" );
        content.getData().setString( "crimes[0].year", "1989" );
        content.getData().setString( "crimes[1].description", "Stole a chocolate from the Matbua shop" );
        content.getData().setString( "crimes[1].year", "1990" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "personalia", 1, 0 );
    }

    @Test
    public void given_input_with_maxOccur1_with_two_nonnull_data_when_validate_then_MaximumOccurrencesValidationError()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "1" );
        content.getData().setString( "myInput[1]", "2" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream() ).allMatch( ve -> ve instanceof DataValidationError );
    }

    @Test
    public void given_input_with_maxOccur1_with_three_null_data_when_validate_then_hasErrors_returns_false()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", null );
        content.getData().setString( "myInput[1]", null );
        content.getData().setString( "myInput[2]", null );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_input_with_maxOccur1_with_one_null_and_one_nonnull_value_when_validate_then_hasErrors_returns_false()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "1" );
        content.getData().setString( "myInput[1]", null );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_input_with_minOccur1_with_two_null_values_when_validate_then_hasErrors_returns_MinimumOccurrencesValidationError()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", null );
        content.getData().setString( "myInput[1]", null );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertTrue( validationResults.hasErrors() );
        assertThat( validationResults.stream() ).allMatch( ve -> ve instanceof DataValidationError );
    }

    @Test
    public void given_input_with_minOccur1_with_one_null_value_when_validate_then_hasErrors_returns_MinimumOccurrencesValidationError()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", null );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertTrue( validationResults.hasErrors() );
        assertThat( validationResults.stream() ).allMatch( ve -> ve instanceof DataValidationError );
    }

    @Test
    public void given_input_with_minOccur1_with_two_nonnull_values_when_validate_then_hasErrors_returns_false()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", "1" );
        content.getData().setString( "myInput[1]", null );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_input_with_minOccur1_and_maxOccur2_with_one_null_value_when_validate_then_hasErrors_returns_MinimumOccurrencesValidationError()
    {
        contentType.getForm()
            .getFormItems()
            .add( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myInput[0]", null );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertTrue( validationResults.hasErrors() );
        assertThat( validationResults.stream() ).allMatch( ve -> ve instanceof DataValidationError );
    }

    @Test
    public void given_formitemset_with_input_with_minOccur1_with_one_null_value_when_validate_then_hasErrors_returns_MinimumOccurrencesValidationError()
    {
        FormItemSet crimes = FormItemSet.create().name( "crimes" ).multiple( true ).build();
        contentType.getForm().getFormItems().add( crimes );
        crimes.add( Input.create()
                        .name( "description" )
                        .label( "Description" )
                        .minimumOccurrences( 1 )
                        .inputType( InputTypeName.TEXT_LINE )
                        .build() );
        crimes.add( Input.create().name( "year" ).label( "Year" ).inputType( InputTypeName.TEXT_LINE ).build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "crimes[0].description", null );
        content.getData().setString( "crimes[0].year", "1989" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertTrue( validationResults.hasErrors() );
        assertThat( validationResults.stream() ).allMatch( ve -> ve instanceof DataValidationError );
    }

    @Test
    public void given_formitemset_with_two_inputs_with_minOccur_with_data_when_validate_then_hasErrors_returns_false()
    {
        FormItemSet crimes = FormItemSet.create().name( "crimes" ).multiple( true ).build();
        contentType.getForm().getFormItems().add( crimes );
        crimes.add( Input.create()
                        .name( "description" )
                        .label( "Description" )
                        .minimumOccurrences( 1 )
                        .inputType( InputTypeName.TEXT_LINE )
                        .build() );
        crimes.add( Input.create()
                        .name( "year" )
                        .label( "Year" )
                        .minimumOccurrences( 2 )
                        .maximumOccurrences( 0 )
                        .inputType( InputTypeName.TEXT_LINE )
                        .build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "crimes[0].description", "descr" );
        content.getData().setString( "crimes[0].year[0]", "1989" );
        content.getData().setString( "crimes[0].year[1]", "1990" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_formoptionset_with_input_with_minOccur1_with_one_null_value_when_validate_then_hasErrors_returns_MinimumOccurrencesValidationError()
    {
        FormOptionSet.Builder crimes = FormOptionSet.create().occurrences( Occurrences.create( 1, 0 ) ).name( "crimes" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );

        option1.addFormItem( Input.create()
                                 .name( "description" )
                                 .label( "Description" )
                                 .minimumOccurrences( 1 )
                                 .inputType( InputTypeName.TEXT_LINE )
                                 .build() );
        option1.addFormItem( Input.create().name( "year" ).label( "Year" ).inputType( InputTypeName.TEXT_LINE ).build() );

        crimes.addOptionSetOption( option1.build() );
        contentType.getForm().getFormItems().add( crimes.build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "crimes[0].option1.description", null );
        content.getData().setString( "crimes[0].option1.year", "1989" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }


    @Test
    public void given_formoptionset_with_two_inputs_with_minOccur_with_data_when_validate_then_hasErrors_returns_false()
    {
        FormOptionSet.Builder crimes = FormOptionSet.create().occurrences( Occurrences.create( 1, 0 ) ).name( "crimes" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );

        option1.addFormItem( Input.create()
                                 .name( "description" )
                                 .label( "Description" )
                                 .minimumOccurrences( 1 )
                                 .inputType( InputTypeName.TEXT_LINE )
                                 .build() );
        option1.addFormItem( Input.create()
                                 .name( "year" )
                                 .label( "Year" )
                                 .minimumOccurrences( 2 )
                                 .maximumOccurrences( 0 )
                                 .inputType( InputTypeName.TEXT_LINE )
                                 .build() );

        crimes.addOptionSetOption( option1.build() );
        contentType.getForm().getFormItems().add( crimes.build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "crimes[0].option1.description", "descr" );
        content.getData().setString( "crimes[0].option1.year[0]", "1989" );
        content.getData().setString( "crimes[0].option1.year[1]", "1990" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test()
    public void given_required_optionset_with_data_when_validate_then_hasErrors_returns_false()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().occurrences( Occurrences.create( 1, 0 ) ).name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );

        option1.addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() );

        myOptionSet.addOptionSetOption( option1.build() );
        contentType.getForm().getFormItems().add( myOptionSet.build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myInput", "value" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_optionset_with_no_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().occurrences( Occurrences.create( 1, 0 ) ).name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );

        option1.addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() );

        myOptionSet.addOptionSetOption( option1.build() );
        contentType.getForm().getFormItems().add( myOptionSet.build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "myOptionSet", 1, 0 );
    }

    @Test
    public void given_required_optionset_with_no_data_within_layout_when_validate_then_MinimumOccurrencesValidationError()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().occurrences( Occurrences.create( 1, 0 ) ).name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );

        option1.addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() );

        myOptionSet.addOptionSetOption( option1.build() );

        contentType.getForm()
            .getFormItems()
            .add( FieldSet.create().label( "My layout" ).name( "myLayout" ).addFormItem( myOptionSet.build() ).build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertTrue( validationResults.hasErrors() );
        assertThat( validationResults.stream() ).allMatch( ve -> ve instanceof DataValidationError );
    }

    @Test
    public void given_required_input_at_top_and_inside_formOptionSet_and_formOptionSet_have_other_unrequired_data_when_validate_then_two_errors_are_found()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().occurrences( Occurrences.create( 1, 0 ) ).name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );

        option1.addFormItem(
            Input.create().name( "myRequiredInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).required( true ).build() );

        myOptionSet.addOptionSetOption( option1.build() );

        contentType.getForm().getFormItems().add( myOptionSet.build() );

        contentType.getForm()
            .getFormItems()
            .add( Input.create()
                      .name( "myOtherRequiredInput" )
                      .label( "Other input" )
                      .inputType( InputTypeName.TEXT_LINE )
                      .required( true )
                      .build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );

        // exercise
        final ValidationErrors validationResults = validate( content );

        assertThat( validationResults.stream() ).hasSize( 1 );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "myOtherRequiredInput", 1, 0 );
    }


    @Test
    public void data_for_input_is_not_required_if_parent_optionset_does_not_exist()
    {
        FormOptionSet.Builder myOptionSet = FormOptionSet.create().required( false ).name( "myOptionSet" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );

        option1.addFormItem( Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build() );

        myOptionSet.addOptionSetOption( option1.build() );

        contentType.getForm().getFormItems().add( myOptionSet.build() );
        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myData", "1" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_required_optionset_with_no_data_and_other_optionset_with_data_when_validate_then_MinimumOccurrencesValidationError()
    {
        FormOptionSet.Builder personalia = FormOptionSet.create().multiple( false ).required( true ).name( "personalia" );

        FormOptionSetOption.Builder option1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );

        option1.addFormItem( Input.create().name( "eyeColour" ).label( "Eye color" ).inputType( InputTypeName.TEXT_LINE ).build() );
        option1.addFormItem( Input.create().name( "hairColour" ).label( "Hair color" ).inputType( InputTypeName.TEXT_LINE ).build() );

        personalia.addOptionSetOption( option1.build() );
        contentType.getForm().getFormItems().add( personalia.build() );

        FormOptionSet.Builder crimes = FormOptionSet.create().multiple( false ).name( "crimes" );

        FormOptionSetOption.Builder crimesOption1 = FormOptionSetOption.create().name( "option1" ).defaultOption( true );

        crimesOption1.addFormItem(
            Input.create().name( "description" ).label( "Description" ).inputType( InputTypeName.TEXT_LINE ).build() );
        crimesOption1.addFormItem( Input.create().name( "year" ).label( "Year" ).inputType( InputTypeName.TEXT_LINE ).build() );

        personalia.addOptionSetOption( crimesOption1.build() );
        contentType.getForm().getFormItems().add( crimes.build() );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        content.getData().setString( "name", "Thomas" );
        content.getData().setString( "crimes[0].option1.description", "Stole tomatoes from neighbour" );
        content.getData().setString( "crimes[0].option1..year", "1989" );
        content.getData().setString( "crimes[1].option1..description", "Stole a chocolate from the Matbua shop" );
        content.getData().setString( "crimes[1].option1..year", "1990" );

        // exercise
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "personalia", 1, 0 );
    }

    @Test
    public void given_optionset_with_default_selection_passes_multiselection_check()
    {
        contentType.getForm().getFormItems().add( makeOptionSet( "myOptionSet", 1, 1, 1, 1 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void given_optionset_with_required_selection_and_empty_selection_array_fails_multiselection_check()
    {
        contentType.getForm().getFormItems().add( makeOptionSet( "myOptionSet", 0, 0, 1, 1 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );
        content.getData().setString( "myOptionSet._selected", "1" );
        content.getData().removeProperty( "myOptionSet._selected" );
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "myOptionSet", 1, 1, 0 );
    }

    @Test
    public void given_optionset_with_required_selection_selection_array_hast_too_many_selected_options()
    {
        contentType.getForm().getFormItems().add( makeOptionSet( "myOptionSet", 3, 0, 1, 2 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );
        content.getData().getSet( "myOptionSet" ).addString( "_selected", "option1" );
        content.getData().getSet( "myOptionSet" ).addString( "_selected", "option2" );
        content.getData().getSet( "myOptionSet" ).addString( "_selected", "option3" );

        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "myOptionSet", 1, 2, 3 );
    }

    @Test
    public void given_optionset_with_required_selection_selection_array_hast_too_few_selected_options()
    {
        contentType.getForm().getFormItems().add( makeOptionSet( "myOptionSet", 3, 0, 3, 3 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );
        content.getData().getSet( "myOptionSet" ).addString( "_selected", "option1" );
        content.getData().getSet( "myOptionSet" ).addString( "_selected", "option2" );

        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "myOptionSet", 3, 3, 2 );
    }

    @Test
    public void given_optionset_with_required_selection_and_missing_selection_array_has_too_many_default_options()
    {
        contentType.getForm().getFormItems().add( makeOptionSet( "myOptionSet", 0, 2, 1, 1 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );
        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "myOptionSet", 1, 1, 2 );
    }

    @Test
    public void given_optionset_with_required_selection_and_missing_selection_array_has_too_little_default_options()
    {
        contentType.getForm().getFormItems().add( makeOptionSet( "myOptionSet", 2, 0, 1, 1 ) );

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setString( "myOptionSet.option1.myUnrequiredData", "1" );

        final ValidationErrors validationResults = validate( content );
        assertThat( validationResults.stream().findFirst() ).containsInstanceOf( DataValidationError.class )
            .get()
            .extracting( ValidationError::getArgs, LIST )
            .containsExactly( "myOptionSet", 1, 1, 0 );
    }

    private FormOptionSet makeOptionSet( final String name, int numberOfOptions, int numberOfDefaultOptions, int minSelection,
                                         int maxSelection )
    {
        FormOptionSet.Builder myOptionSet =
            FormOptionSet.create().required( false ).multiselection( Occurrences.create( minSelection, maxSelection ) ).name( name );

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

    @Test
    public void testOptionSetWithDefaultValue()
    {
        Form form = Form.create()
            .addFormItem( FormOptionSet.create()
                              .name( "checkOptionSet" )
                              .label( "Multi selection" )
                              .expanded( true )
                              .helpText( "Help Text" )
                              .multiple( true )
                              .multiselection( Occurrences.create( 1, 3 ) )
                              .occurrences( Occurrences.create( 1, 1 ) )
                              .addOptionSetOption( FormOptionSetOption.create()
                                                       .name( "option_1" )
                                                       .label( "option_1" )
                                                       .helpText( "Help text for Option_1" )
                                                       .build() )
                              .addOptionSetOption( FormOptionSetOption.create()
                                                       .name( "option_2" )
                                                       .label( "option_2" )
                                                       .helpText( "Help text for Option_2" )
                                                       .build() )
                              .addOptionSetOption( FormOptionSetOption.create()
                                                       .name( "option_3" )
                                                       .label( "option_3" )
                                                       .defaultOption( true )
                                                       .helpText( "Help text for Option_3" )
                                                       .addFormItem( Input.create()
                                                                         .name( "htmlAreaField" )
                                                                         .label( "Input" )
                                                                         .inputType( InputTypeName.HTML_AREA )
                                                                         .occurrences( Occurrences.create( 2, 4 ) )
                                                                         .build() )
                                                       .build() )
                              .build() )
            .build();

        ContentType contentType =
            ContentType.create().name( "myapplication:my_type" ).superType( ContentTypeName.structured() ).form( form ).build();

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setSet( "checkOptionSet.option_3", content.getData().newSet() );
        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void testOptionSetWithUnlimitedNumberAllowedSelections()
    {
        Form form = Form.create()
            .addFormItem( FormOptionSet.create()
                              .name( "options" )
                              .label( "Option tests" )
                              .multiselection( Occurrences.create( 0, 0 ) )
                              .occurrences( Occurrences.create( 1, 1 ) )
                              .addOptionSetOption( FormOptionSetOption.create().name( "a" ).label( "A" ).build() )
                              .addOptionSetOption( FormOptionSetOption.create().name( "b" ).label( "B" ).build() )
                              .build() )
            .build();

        ContentType contentType =
            ContentType.create().name( "myapplication:my_type" ).superType( ContentTypeName.structured() ).form( form ).build();

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();

        PropertySet propertySet = content.getData().newSet();
        propertySet.setProperty( "a", ValueFactory.newString( "Value A" ) );
        propertySet.setProperty( "b", ValueFactory.newString( "Value B" ) );
        content.getData().setSet( "options", propertySet );

        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }

    @Test
    public void testOptionSetWithUnlimitedNumberAllowedSelectionsAndDefaultValues()
    {
        ContentType contentType = ContentType.create()
            .name( "myapplication:my_type" )
            .superType( ContentTypeName.structured() )
            .form( Form.create()
                       .addFormItem( FormOptionSet.create()
                                         .name( "options" )
                                         .label( "Option tests" )
                                         .multiselection( Occurrences.create( 0, 0 ) )
                                         .occurrences( Occurrences.create( 1, 1 ) )
                                         .addOptionSetOption( FormOptionSetOption.create().name( "a" ).label( "A" ).build() )
                                         .addOptionSetOption(
                                             FormOptionSetOption.create().name( "b" ).label( "B" ).defaultOption( true ).build() )
                                         .build() )
                       .build() )
            .build();

        Content content = Content.create().path( MY_CONTENT_PATH ).type( contentType.getName() ).build();
        content.getData().setSet( "options", content.getData().newSet() );

        final ValidationErrors validationResults = validate( content );
        assertFalse( validationResults.hasErrors() );
    }
}
