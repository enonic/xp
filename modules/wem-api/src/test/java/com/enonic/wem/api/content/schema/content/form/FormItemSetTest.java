package com.enonic.wem.api.content.schema.content.form;


import org.junit.Test;

import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;

import static com.enonic.wem.api.content.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static org.junit.Assert.*;

public class FormItemSetTest
{
    @Test
    public void copy()
    {
        // setup
        FormItemSet original = newFormItemSet().name( "name" ).label( "Label" ).multiple( true ).build();
        original.add( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise
        FormItemSet copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "name", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertNotSame( original.getFormItems(), copy.getFormItems() );
        assertNotSame( original.getInput( new FormItemPath( "myField" ) ), copy.getInput( new FormItemPath( "myField" ) ) );
    }

    @Test
    public void getConfig()
    {
        // setup
        FormItemSet formItemSet = newFormItemSet().name( "mySet" ).label( "Label" ).multiple( true ).build();
        formItemSet.add( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise
        HierarchicalFormItem field = formItemSet.getInput( new FormItemPath( "myField" ) );

        // verify
        assertEquals( "mySet.myField", field.getPath().toString() );
    }

    @Test
    public void setPath()
    {
        FormItemSet formItemSet = newFormItemSet().name( "address" ).label( "Address" ).build();
        formItemSet.add( newInput().name( "street" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "postalCode" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "postalPlace" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "country" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        formItemSet.setName( "homeAddress" );
        formItemSet.setPath( new FormItemPath( "homeAddress" ) );

        // verify
        assertEquals( "homeAddress.street", formItemSet.getInput( new FormItemPath( "street" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalCode", formItemSet.getInput( new FormItemPath( "postalCode" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalPlace", formItemSet.getInput( new FormItemPath( "postalPlace" ) ).getPath().toString() );
        assertEquals( "homeAddress.country", formItemSet.getInput( new FormItemPath( "country" ) ).getPath().toString() );
    }


    @Test
    public void toFormItemSet_given_FormItem_of_type_FormItemSet_then_FormItemSet_is_returned()
    {
        // setup
        FormItem formItem = newFormItemSet().name( "myFieldSet" ).label( "My label" ).build();

        // exercise
        FormItemSet formItemSet = formItem.toFormItemSet();

        // verify
        assertSame( formItem, formItemSet );
    }

    @Test
    public void toFormItemSet_given_FormItem_of_type_Input_then_exception_is_thrown()
    {
        // setup
        FormItem formItem = newInput().name( "myFieldSet" ).type( InputTypes.DATE ).label( "My label" ).build();

        // exercise
        try
        {
            formItem.toFormItemSet();
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "This FormItem [myFieldSet] is not a FormItemSet: Input", e.getMessage() );
        }
    }
}
