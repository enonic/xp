package com.enonic.wem.api.content.type;


import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.type.component.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.component.Input.newInput;
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

    @Test()
    public void given_required_field_with_data_when_verify_then_exception_is_not_thrown()
    {
        contentType.addComponent( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myField", "value" );

        // exercise
        try
        {
            new OccurrenceValidator( contentType ).verify( content.getData() );
        }
        catch ( Exception e )
        {
            fail( "No exception expected" );
        }
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_when_verify_then_exception_is_thrown()
    {

        contentType.addComponent( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        new OccurrenceValidator( contentType ).verify( content.getData() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_within_layout_when_verify_then_exception_is_thrown()
    {

        contentType.addComponent( newFieldSet().label( "Label" ).name( "myLayout" ).add(
            newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() );
        Content content = new Content();

        // exercise
        new OccurrenceValidator( contentType ).verify( content.getData() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_within_layout_within_layout_when_verify_then_exception_is_thrown()
    {

        contentType.addComponent( newFieldSet().label( "My outer layout" ).name( "myOuterlayout" ).add(
            newFieldSet().label( "My Layout" ).name( "myLayout" ).add(
                newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        new OccurrenceValidator( contentType ).verify( content.getData() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_within_fieldSet_within_layout_when_verify_then_exception_is_thrown()
    {
        contentType.addComponent( newFieldSet().label( "My layout" ).name( "myLayout" ).add(
            newComponentSet().name( "myFieldSet" ).required( true ).add(
                newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setData( "myFieldSet.myField", "" );

        // exercise
        new OccurrenceValidator( contentType ).verify( content.getData() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_within_fieldSet_when_verify_then_exception_is_thrown()
    {
        contentType.addComponent( newComponentSet().name( "myFieldSet" ).required( true ).add(
            newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myFieldSet.myField", "" );

        // exercise
        new OccurrenceValidator( contentType ).verify( content.getData() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_field_with_no_data_within_layout_within_a_fieldSet_when_verify_then_exception_is_thrown()
    {
        contentType.addComponent( newComponentSet().name( "myFieldSet" ).required( true ).add(
            newFieldSet().label( "My FieldSet" ).name( "myFieldSet" ).add(
                newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).required( true ).build() ).build() ).build() );
        Content content = new Content();
        content.setData( "myFieldSet.myField", "" );

        // exercise
        new OccurrenceValidator( contentType ).verify( content.getData() );
    }

    @Test()
    public void given_required_fieldSet_with_data_when_verify_then_exception_is_not_thrown()
    {
        contentType.addComponent( newComponentSet().name( "myFieldSet" ).required( true ).add(
            newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );
        content.setData( "myFieldSet.myField", "value" );

        // exercise
        try
        {
            new OccurrenceValidator( contentType ).verify( content.getData() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail( "No exception expected: " + e );
        }

    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_fieldSet_with_no_data_when_verify_then_exception_is_thrown()
    {
        contentType.addComponent( newComponentSet().name( "myFieldSet" ).required( true ).add(
            newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        new OccurrenceValidator( contentType ).verify( content.getData() );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void given_required_fieldSet_with_no_data_within_layout_when_verify_then_exception_is_thrown()
    {
        contentType.addComponent( newFieldSet().label( "My FieldSet" ).name( "myFieldSet" ).add(
            newComponentSet().name( "myFieldSet" ).required( true ).add(
                newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build() );
        Content content = new Content();
        content.setType( contentType.getQualifiedName() );

        // exercise
        new OccurrenceValidator( contentType ).verify( content.getData() );
    }

    @Test
    public void given_required_set_with_no_data_and_other_set_with_data_when_verify_then_exception_is_thrown()
    {
        // setup
        contentType.addComponent( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );

        ComponentSet personaliaComponentSet = newComponentSet().name( "personalia" ).multiple( false ).required( true ).build();
        personaliaComponentSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        personaliaComponentSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );
        contentType.addComponent( personaliaComponentSet );

        ComponentSet crimesComponentSet = newComponentSet().name( "crimes" ).multiple( true ).build();
        contentType.addComponent( crimesComponentSet );
        crimesComponentSet.add( newInput().name( "description" ).type( InputTypes.TEXT_LINE ).build() );
        crimesComponentSet.add( newInput().name( "year" ).type( InputTypes.TEXT_LINE ).build() );

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
            new OccurrenceValidator( contentType ).verify( content.getData() );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof BreaksRequiredContractException );
            assertEquals( "Required contract is broken, data missing for ComponentSet: personalia", e.getMessage() );
        }
    }

}
