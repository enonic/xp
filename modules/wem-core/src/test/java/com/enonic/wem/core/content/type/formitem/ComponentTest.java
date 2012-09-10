package com.enonic.wem.core.content.type.formitem;


import org.junit.Test;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.formitem.comptype.ComponentTypes;

import static org.junit.Assert.*;

public class ComponentTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void breaksRequiredContract_throws_exception_when_broken()
    {
        Component component = Component.newBuilder().name( "myTextLine" ).type( ComponentTypes.TEXT_LINE ).required( true ).build();
        component.checkBreaksRequiredContract( Data.newData().type( DataTypes.TEXT ).value( null ).build() );
    }

    @Test
    public void copy()
    {
        // setup
        Component original = Component.newBuilder().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build();

        // exercise
        Component copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "myField", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertSame( original.getCustomText(), copy.getCustomText() );
        assertSame( original.getComponentType(), copy.getComponentType() );
    }
}
