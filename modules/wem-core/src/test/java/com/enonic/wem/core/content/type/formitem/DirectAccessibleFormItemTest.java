package com.enonic.wem.core.content.type.formitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.formitem.fieldtype.ComponentTypes;

import static org.junit.Assert.*;

public class DirectAccessibleFormItemTest
{
    @Test
    public void setParentPath()
    {
        Component component = Component.newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build();
        component.setParentPath( new FormItemPath( "myParent" ) );
        assertEquals( "myParent.myField", component.getPath().toString() );

        component = Component.newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build();
        component.setPath( new FormItemPath( "myParent.myField" ) );
        component.setParentPath( new FormItemPath( "myNewParent" ) );
        assertEquals( "myNewParent.myField", component.getPath().toString() );

        component = Component.newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build();
        component.setParentPath( new FormItemPath( "myGrandParent.myParent" ) );
        assertEquals( "myGrandParent.myParent.myField", component.getPath().toString() );

        component = Component.newComponent().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build();
        component.setPath( new FormItemPath( "myParent.myField" ) );
        component.setParentPath( new FormItemPath( "myNewGrandParent.myNewParent" ) );
        assertEquals( "myNewGrandParent.myNewParent.myField", component.getPath().toString() );
    }
}
