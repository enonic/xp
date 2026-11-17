package com.enonic.xp.core.impl.content.validate;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.DataValidationError;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeNotFoundException;
import com.enonic.xp.util.GenericValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InputValueValidatorTest
{
    private static final String VALUE_INVALID_I18N = "system.cms.validation.valueInvalid";

    private static final String VALUE_INVALID_CODE = "system:cms.valueInvalid";

    @Test
    void valid_data_produces_no_errors()
    {
        final Form form = Form.create()
            .addFormItem( comboBox( "color", "red", "blue" ) )
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addString( "color", "red" );
        data.addString( "title", "Hello" );

        assertThat( validate( form, data ).hasErrors() ).isFalse();
    }

    @Test
    void combobox_value_not_in_options_produces_error()
    {
        final Form form = Form.create().addFormItem( comboBox( "color", "red", "blue" ) ).build();

        final PropertyTree data = new PropertyTree();
        data.addString( "color", "pink" );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream() ).hasSize( 1 );
        assertValueInvalidError( errors.stream().findFirst().orElseThrow(), "color",
                                 "Invalid value in [color: pink]: Value is not a valid option" );
    }

    @Test
    void radiobutton_value_not_in_options_produces_error()
    {
        final Form form = Form.create()
            .addFormItem( Input.create()
                              .name( "size" )
                              .label( "Size" )
                              .inputType( InputTypeName.RADIO_BUTTON )
                              .inputTypeProperty( "options", options( "small", "large" ) )
                              .build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addString( "size", "medium" );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream() ).hasSize( 1 );
        assertValueInvalidError( errors.stream().findFirst().orElseThrow(), "size",
                                 "Invalid value in [size: medium]: Value is not a valid option" );
    }

    @Test
    void textline_regexp_mismatch_produces_error()
    {
        final Form form = Form.create()
            .addFormItem( Input.create()
                              .name( "code" )
                              .label( "Code" )
                              .inputType( InputTypeName.TEXT_LINE )
                              .inputTypeProperty( "regexp", "^[a-z]+$" )
                              .build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addString( "code", "ABC" );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream() ).hasSize( 1 );
        assertValueInvalidError( errors.stream().findFirst().orElseThrow(), "code",
                                 "Invalid value in [code: ABC]: Value does not match with regular expression" );
    }

    @Test
    void textline_exceeding_max_length_produces_error()
    {
        final Form form = Form.create()
            .addFormItem( Input.create()
                              .name( "code" )
                              .label( "Code" )
                              .inputType( InputTypeName.TEXT_LINE )
                              .inputTypeProperty( "maxLength", "3" )
                              .build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addString( "code", "abcd" );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream() ).hasSize( 1 );
        assertValueInvalidError( errors.stream().findFirst().orElseThrow(), "code",
                                 "Invalid value in [code: abcd]: Limit of 3 characters exceeded" );
    }

    @Test
    void long_outside_min_max_produces_errors()
    {
        final Form form = Form.create()
            .addFormItem( Input.create()
                              .name( "count" )
                              .label( "Count" )
                              .inputType( InputTypeName.LONG )
                              .inputTypeProperty( "min", "2" )
                              .inputTypeProperty( "max", "5" )
                              .build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addLong( "count", 1L );
        data.addLong( "count", 7L );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream() ).hasSize( 2 );
        assertValueInvalidError( errors.stream().toList().get( 0 ), "count", "Invalid value in [count: 1]: The value cannot be less than 2" );
        assertValueInvalidError( errors.stream().toList().get( 1 ), "count[1]",
                                 "Invalid value in [count: 7]: The value cannot be greater than 5" );
    }

    @Test
    void double_outside_range_produces_error()
    {
        final Form form = Form.create()
            .addFormItem( Input.create()
                              .name( "ratio" )
                              .label( "Ratio" )
                              .inputType( InputTypeName.DOUBLE )
                              .inputTypeProperty( "max", "1.5" )
                              .build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addDouble( "ratio", 2.0 );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream() ).hasSize( 1 );
        assertThat( errors.stream().findFirst().orElseThrow().getArgs() ).containsExactly(
            "Invalid value in [ratio: 2.0]: The value cannot be greater than 1.5" );
    }

    @Test
    void wrong_value_type_produces_error()
    {
        final Form form = Form.create()
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addLong( "title", 42L );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream() ).hasSize( 1 );
        assertValueInvalidError( errors.stream().findFirst().orElseThrow(), "title",
                                 "Invalid type in [title: 42]: [Long] instead of [Long,String]" );
    }

    @Test
    void every_invalid_input_produces_its_own_error()
    {
        final Form form = Form.create()
            .addFormItem( comboBox( "color", "red", "blue" ) )
            .addFormItem( comboBox( "size", "small", "large" ) )
            .addFormItem( Input.create().name( "title" ).label( "Title" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addString( "color", "pink" );
        data.addString( "size", "medium" );
        data.addString( "title", "Hello" );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream().map( e -> ( (DataValidationError) e ).getPropertyPath().toString() ) ).containsExactly( "color",
                                                                                                                              "size" );
    }

    @Test
    void only_the_invalid_occurrence_of_an_array_is_reported()
    {
        final Form form = Form.create().addFormItem( comboBox( "tags", "a", "b" ) ).build();

        final PropertyTree data = new PropertyTree();
        data.addString( "tags", "a" );
        data.addString( "tags", "c" );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream() ).hasSize( 1 );
        assertValueInvalidError( errors.stream().findFirst().orElseThrow(), "tags[1]",
                                 "Invalid value in [tags: c]: Value is not a valid option" );
    }

    @Test
    void input_inside_item_set_is_reported_with_full_path()
    {
        final Form form = Form.create()
            .addFormItem( FormItemSet.create().name( "set" ).label( "Set" ).addFormItem( comboBox( "color", "red", "blue" ) ).build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addSet( "set" ).addString( "color", "pink" );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream() ).hasSize( 1 );
        assertValueInvalidError( errors.stream().findFirst().orElseThrow(), "set.color",
                                 "Invalid value in [color: pink]: Value is not a valid option" );
    }

    @Test
    void input_inside_option_set_option_is_reported_with_full_path()
    {
        final Form form = Form.create()
            .addFormItem( FormOptionSet.create()
                              .name( "options" )
                              .label( "Options" )
                              .addOptionSetOption( FormOptionSetOption.create()
                                                       .name( "option1" )
                                                       .label( "Option 1" )
                                                       .addFormItem( comboBox( "color", "red", "blue" ) )
                                                       .build() )
                              .build() )
            .build();

        final PropertyTree data = new PropertyTree();
        final PropertySet options = data.addSet( "options" );
        options.addString( "_selected", "option1" );
        options.addSet( "option1" ).addString( "color", "pink" );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream() ).hasSize( 1 );
        assertValueInvalidError( errors.stream().findFirst().orElseThrow(), "options.option1.color",
                                 "Invalid value in [color: pink]: Value is not a valid option" );
    }

    @Test
    void input_inside_field_set_is_validated()
    {
        final Form form = Form.create()
            .addFormItem( FieldSet.create().label( "Fields" ).addFormItem( comboBox( "color", "red", "blue" ) ).build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addString( "color", "pink" );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream() ).hasSize( 1 );
        assertValueInvalidError( errors.stream().findFirst().orElseThrow(), "color",
                                 "Invalid value in [color: pink]: Value is not a valid option" );
    }

    @Test
    void unknown_input_type_without_data_produces_no_error()
    {
        final Form form = Form.create()
            .addFormItem( Input.create().name( "custom" ).label( "Custom" ).inputType( InputTypeName.from( "Unknown" ) ).build() )
            .build();

        assertThat( validate( form, new PropertyTree() ).hasErrors() ).isFalse();
    }

    @Test
    void unknown_input_type_with_data_throws()
    {
        final Form form = Form.create()
            .addFormItem( Input.create().name( "custom" ).label( "Custom" ).inputType( InputTypeName.from( "Unknown" ) ).build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addString( "custom", "value" );

        assertThatThrownBy( () -> validate( form, data ) ).isInstanceOf( InputTypeNotFoundException.class );
    }

    @Test
    void message_is_the_exception_text_without_formatting()
    {
        final Form form = Form.create()
            .addFormItem( Input.create()
                              .name( "code" )
                              .label( "Code" )
                              .inputType( InputTypeName.TEXT_LINE )
                              .inputTypeProperty( "regexp", "^[a-z]+$" )
                              .build() )
            .build();

        final PropertyTree data = new PropertyTree();
        data.addString( "code", "{0}'s" );

        final ValidationErrors errors = validate( form, data );

        assertThat( errors.stream().findFirst().orElseThrow().getMessage() ).isEqualTo(
            "Invalid value in [code: {0}'s]: Value does not match with regular expression" );
    }

    @Test
    void null_value_produces_no_error()
    {
        final Form form = Form.create().addFormItem( comboBox( "color", "red", "blue" ) ).build();

        final PropertyTree data = new PropertyTree();
        data.addString( "color", null );

        assertThat( validate( form, data ).hasErrors() ).isFalse();
    }

    private static ValidationErrors validate( final Form form, final PropertyTree data )
    {
        final ValidationErrors.Builder builder = ValidationErrors.create();
        InputValueValidator.validate( form, data, ( errorCode, propertyPath, i18n ) -> ValidationError.dataError( errorCode, propertyPath )
            .i18n( i18n ), builder );
        return builder.build();
    }

    private static void assertValueInvalidError( final ValidationError error, final String expectedPropertyPath,
                                                 final String expectedMessage )
    {
        assertThat( error ).isInstanceOf( DataValidationError.class );
        assertThat( error.getErrorCode().toString() ).isEqualTo( VALUE_INVALID_CODE );
        assertThat( error.getI18n() ).isEqualTo( VALUE_INVALID_I18N );
        assertThat( error.getMessage() ).isEqualTo( expectedMessage );
        assertThat( error.getArgs() ).containsExactly( expectedMessage );
        assertThat( ( (DataValidationError) error ).getPropertyPath().toString() ).isEqualTo( expectedPropertyPath );
    }

    private static Input comboBox( final String name, final String... values )
    {
        return Input.create()
            .name( name )
            .label( name )
            .inputType( InputTypeName.COMBO_BOX )
            .inputTypeProperty( "options", options( values ) )
            .build();
    }

    private static GenericValue options( final String... values )
    {
        final GenericValue.ListBuilder options = GenericValue.newList();
        for ( final String value : values )
        {
            options.add( GenericValue.newObject()
                             .put( "value", value )
                             .put( "label", GenericValue.newObject().put( "text", value ).build() )
                             .build() );
        }
        return options.build();
    }
}
