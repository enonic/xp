package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class FieldTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_throws_exception_when_broken()
    {
        Field field = Field.newBuilder().name( "myTextLine" ).type( FieldTypes.TEXT_LINE ).required( true ).build();
        field.checkBreaksRequiredContract( Data.newData().field( field ).value( null ).build() );
    }

    @Test
    public void copy()
    {
        // setup
        Field original = Field.newBuilder().name( "myField" ).type( FieldTypes.TEXT_LINE ).build();

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
