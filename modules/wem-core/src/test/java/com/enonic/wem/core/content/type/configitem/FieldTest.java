package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import com.enonic.wem.core.content.data.Value;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class FieldTest
{
    @Test
    public void breaksRequiredContract()
    {
        Field field = Field.newBuilder().name( "myTextLine" ).type( FieldTypes.textline ).required( true ).build();

        assertEquals( true, field.breaksRequiredContract( Value.newBuilder().field( field ).value( null ).build() ) );
        assertEquals( true, field.breaksRequiredContract( Value.newBuilder().field( field ).value( "" ).build() ) );
        assertEquals( true, field.breaksRequiredContract( Value.newBuilder().field( field ).value( " " ).build() ) );
        assertEquals( false, field.breaksRequiredContract( Value.newBuilder().field( field ).value( "something" ).build() ) );
    }

    @Test
    public void copy()
    {
        // setup
        Field original = Field.newBuilder().name( "myField" ).type( FieldTypes.textline ).build();

        // exercise
        Field copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "myField", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertSame( original.getCustomText(), copy.getCustomText() );
        assertSame( original.getFieldType(), copy.getFieldType() );
    }
}
