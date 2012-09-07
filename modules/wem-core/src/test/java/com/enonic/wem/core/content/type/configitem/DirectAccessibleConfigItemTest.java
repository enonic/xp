package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class DirectAccessibleConfigItemTest
{
    @Test
    public void setParentPath()
    {
        Component component = Component.newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).build();
        component.setParentPath( new FormItemPath( "myParent" ) );
        assertEquals( "myParent.myField", component.getPath().toString() );

        component = Component.newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).build();
        component.setPath( new FormItemPath( "myParent.myField" ) );
        component.setParentPath( new FormItemPath( "myNewParent" ) );
        assertEquals( "myNewParent.myField", component.getPath().toString() );

        component = Component.newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).build();
        component.setParentPath( new FormItemPath( "myGrandParent.myParent" ) );
        assertEquals( "myGrandParent.myParent.myField", component.getPath().toString() );

        component = Component.newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).build();
        component.setPath( new FormItemPath( "myParent.myField" ) );
        component.setParentPath( new FormItemPath( "myNewGrandParent.myNewParent" ) );
        assertEquals( "myNewGrandParent.myNewParent.myField", component.getPath().toString() );
    }
}
