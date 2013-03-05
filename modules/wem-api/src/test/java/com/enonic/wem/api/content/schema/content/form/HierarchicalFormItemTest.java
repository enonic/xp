package com.enonic.wem.api.content.schema.content.form;


import org.junit.Test;

import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;

import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static org.junit.Assert.*;

public class HierarchicalFormItemTest
{
    @Test
    public void setParentPath()
    {
        Input input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setParentPath( FormItemPath.from( "myParent" ) );
        assertEquals( "myParent.myField", input.getPath().toString() );

        input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setPath( FormItemPath.from( "myParent.myField" ) );
        input.setParentPath( FormItemPath.from( "myNewParent" ) );
        assertEquals( "myNewParent.myField", input.getPath().toString() );

        input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setParentPath( FormItemPath.from( "myGrandParent.myParent" ) );
        assertEquals( "myGrandParent.myParent.myField", input.getPath().toString() );

        input = newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build();
        input.setPath( FormItemPath.from( "myParent.myField" ) );
        input.setParentPath( FormItemPath.from( "myNewGrandParent.myNewParent" ) );
        assertEquals( "myNewGrandParent.myNewParent.myField", input.getPath().toString() );
    }
}
