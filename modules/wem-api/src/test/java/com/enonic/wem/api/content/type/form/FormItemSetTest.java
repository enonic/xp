package com.enonic.wem.api.content.type.form;


import org.junit.Test;

import com.enonic.wem.api.content.type.form.inputtype.InputTypes;

import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
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
}
