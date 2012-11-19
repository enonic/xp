package com.enonic.wem.api.content.type.form;


import org.junit.Test;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;

import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;

public class InputTest
{
    @Test(expected = MinimumOccurrencesException.class)
    public void breaksRequiredContract_throws_exception_when_broken()
    {
        Input input = newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).required( true ).build();
        Data data = Data.newData().type( DataTypes.TEXT ).value( null ).build();
        input.checkBreaksMinimumOccurrencesContract( data );
    }

    @Test
    public void copy()
    {
        // setup
        Input original = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();

        // exercise
        Input copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "myField", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertSame( original.getCustomText(), copy.getCustomText() );
        assertSame( original.getInputType(), copy.getInputType() );
    }
}
