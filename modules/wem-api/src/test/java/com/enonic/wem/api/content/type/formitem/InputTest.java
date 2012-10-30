package com.enonic.wem.api.content.type.formitem;


import org.junit.Test;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;

import static com.enonic.wem.api.content.type.formitem.Input.newInput;
import static org.junit.Assert.*;

public class InputTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_throws_exception_when_broken()
    {
        Input input = newInput().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).required( true ).build();
        Data data = Data.newData().type( DataTypes.TEXT ).value( null ).build();
        input.checkBreaksRequiredContract( data );
    }

    @Test
    public void copy()
    {
        // setup
        Input original = newInput().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build();

        // exercise
        Input copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "myField", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertSame( original.getCustomText(), copy.getCustomText() );
        assertSame( original.getComponentType(), copy.getComponentType() );
    }
}
