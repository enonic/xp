package com.enonic.wem.api.content.type;


import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.type.form.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.MaximumOccurrencesException;
import com.enonic.wem.api.content.type.form.MinimumOccurrencesException;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.OccurrenceValidator.newOccurrenceValidator;
import static com.enonic.wem.api.content.type.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;

public class OccurrenceValidatorTest
{
    private ContentType contentType = new ContentType();

    @Before
    public void before()
    {
        contentType.setModule( Module.SYSTEM );
        contentType.setName( "MyType" );
    }

    @Test
    public void given_input_with_maxOccur1_with_two_data_when_validate_then_exception_is_thrown()
    {
        contentType.addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).maximumOccurrences( 1 ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myInput[0]", "1" );
        content.setData( "myInput[1]", "2" );

        // exercise
        try
        {
            newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof MaximumOccurrencesException );
            assertEquals( "Input [myInput] allows maximum 1 occurrence: 2", e.getMessage() );
        }
    }

    @Test
    public void given_input_with_maxOccur2_with_three_data_when_validate_then_exception_is_thrown()
    {
        contentType.addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).maximumOccurrences( 2 ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myInput[0]", "1" );
        content.setData( "myInput[1]", "2" );
        content.setData( "myInput[2]", "3" );

        // exercise
        try
        {
            newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof MaximumOccurrencesException );
            assertEquals( "Input [myInput] allows maximum 2 occurrences: 3", e.getMessage() );
        }
    }

    @Test
    public void given_required_input_with_data_when_validate_then_exception_is_not_thrown()
    {
        contentType.addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myInput", "value" );

        // exercise
        try
        {
            newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
        }
        catch ( Exception e )
        {
            fail( "No exception expected" );
        }
    }

    @Test(expected = MinimumOccurrencesException.class)
    public void given_required_input_with_no_data_when_validate_then_exception_is_thrown()
    {
        contentType.addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
    }

    @Test
    public void given_input_with_minOccur1_with_one_data_with_blank_value_when_validate_then_exception_is_thrown()
    {
        contentType.addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).minimumOccurrences( 1 ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myInput", "" );

        // exercise
        try
        {
            newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof MinimumOccurrencesException );
            assertEquals(
                "Input [myInput] requires minimum 1 occurrences. It had 1, but: Required contract for Data [myInput] is broken of type TextLine , value was: ",
                e.getMessage() );
        }
    }

    @Test
    public void given_input_with_minOccur2_with_one_data_when_validate_then_exception_is_thrown()
    {
        contentType.addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).minimumOccurrences( 2 ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myInput", "value" );

        // exercise
        try
        {
            newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof MinimumOccurrencesException );
            assertEquals( "Input [myInput] requires minimum 2 occurrences: 1", e.getMessage() );
        }
    }

    @Test(expected = MinimumOccurrencesException.class)
    public void given_required_field_with_no_data_within_layout_when_validate_then_exception_is_thrown()
    {

        contentType.addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).add(
            newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() );
        Content content = new Content();

        // exercise
        newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
    }

    @Test(expected = MinimumOccurrencesException.class)
    public void given_required_input_with_no_data_within_layout_within_layout_when_validate_then_exception_is_thrown()
    {
        contentType.addFormItem( newFieldSet().label( "My outer layout" ).name( "myOuterlayout" ).add(
            newFieldSet().label( "My Layout" ).name( "myLayout" ).add(
                newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
    }

    @Test(expected = MinimumOccurrencesException.class)
    public void given_required_field_with_no_data_within_set_within_layout_when_validate_then_exception_is_thrown()
    {
        contentType.addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).add(
            newFormItemSet().name( "mySet" ).required( true ).add(
                newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setData( "mySet.myInput", "" );

        // exercise
        newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
    }

    @Test(expected = MinimumOccurrencesException.class)
    public void given_required_input_with_no_data_within_set_when_validate_then_exception_is_thrown()
    {
        contentType.addFormItem( newFormItemSet().name( "mySet" ).required( true ).add(
            newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "mySet.myInput", "" );

        // exercise
        newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
    }

    @Test(expected = MinimumOccurrencesException.class)
    public void given_required_field_with_no_data_within_layout_within_a_set_when_validate_then_exception_is_thrown()
    {
        contentType.addFormItem( newFormItemSet().name( "mySet" ).required( true ).add(
            newFieldSet().label( "My layout" ).name( "myLayout" ).add(
                newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setData( "mySet.myInput", "" );

        // exercise
        newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
    }

    @Test()
    public void given_required_set_with_data_when_validate_then_exception_is_not_thrown()
    {
        contentType.addFormItem( newFormItemSet().name( "mySet" ).required( true ).add(
            newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "mySet.myInput", "value" );

        // exercise
        try
        {
            newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
        }
        catch ( Exception e )
        {
            fail( "No exception expected: " + e );
        }
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_set_with_no_data_when_validate_then_exception_is_thrown()
    {
        contentType.addFormItem( newFormItemSet().name( "mySet" ).required( true ).add(
            newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_set_with_no_data_within_layout_when_validate_then_exception_is_thrown()
    {
        contentType.addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).add(
            newFormItemSet().name( "mySet" ).required( true ).add(
                newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
    }

    @Test
    public void given_required_set_with_no_data_and_other_set_with_data_when_validate_then_exception_is_thrown()
    {
        // setup
        contentType.addFormItem( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );

        FormItemSet personalia = newFormItemSet().name( "personalia" ).multiple( false ).required( true ).build();
        personalia.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        personalia.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );
        contentType.addFormItem( personalia );

        FormItemSet crimes = newFormItemSet().name( "crimes" ).multiple( true ).build();
        contentType.addFormItem( crimes );
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
        try
        {
            newOccurrenceValidator().contentType( contentType ).build().validate( content.getData() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof BreaksRequiredContractException );
            assertEquals( "Required contract is broken, data missing for FormItemSet: personalia", e.getMessage() );
        }
    }

    @Test
    public void given_recordExceptions_is_true_and_invalid_data_when_validate_then_exception_is_recorded()
    {
        contentType.addFormItem( newInput().minimumOccurrences( 1 ).name( "minimumOne" ).type( InputTypes.TEXT_LINE ).build() );
        contentType.addFormItem( newInput().maximumOccurrences( 2 ).name( "maximumTwo" ).type( InputTypes.TEXT_LINE ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "maximumTwo[0]", "1" );
        content.setData( "maximumTwo[1]", "2" );
        content.setData( "maximumTwo[2]", "3" );

        // exercise
        OccurrenceValidator occurrenceValidator = newOccurrenceValidator().contentType( contentType ).recordExceptions( true ).build();
        occurrenceValidator.validate( content.getData() );

        // verify
        Iterator<RuntimeException> recordedExceptions = occurrenceValidator.getRecordedExceptions();
        assertTrue( "No exceptions recorded", recordedExceptions.hasNext() );
        //noinspection ThrowableResultOfMethodCallIgnored
        recordedExceptions.next();
        assertTrue( "Two exceptions expected", recordedExceptions.hasNext() );
    }

}
