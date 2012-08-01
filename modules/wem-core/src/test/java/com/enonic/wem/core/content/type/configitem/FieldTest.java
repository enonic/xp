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
}
