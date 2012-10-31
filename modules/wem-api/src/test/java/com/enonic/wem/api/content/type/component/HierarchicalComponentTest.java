package com.enonic.wem.api.content.type.component;


import org.junit.Test;

import com.enonic.wem.api.content.type.component.inputtype.InputTypes;

import static com.enonic.wem.api.content.type.component.Input.newInput;
import static org.junit.Assert.*;

public class HierarchicalComponentTest
{
    @Test
    public void setParentPath()
    {
        Input input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setParentPath( new ComponentPath( "myParent" ) );
        assertEquals( "myParent.myField", input.getPath().toString() );

        input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setPath( new ComponentPath( "myParent.myField" ) );
        input.setParentPath( new ComponentPath( "myNewParent" ) );
        assertEquals( "myNewParent.myField", input.getPath().toString() );

        input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setParentPath( new ComponentPath( "myGrandParent.myParent" ) );
        assertEquals( "myGrandParent.myParent.myField", input.getPath().toString() );

        input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setPath( new ComponentPath( "myParent.myField" ) );
        input.setParentPath( new ComponentPath( "myNewGrandParent.myNewParent" ) );
        assertEquals( "myNewGrandParent.myNewParent.myField", input.getPath().toString() );
    }
}
