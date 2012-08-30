package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class DirectAccessibleConfigItemTest
{
    @Test
    public void setParentPath()
    {
        Field field = Field.newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).build();
        field.setParentPath( new ConfigItemPath( "myParent" ) );
        assertEquals( "myParent.myField", field.getPath().toString() );

        field = Field.newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).build();
        field.setPath( new ConfigItemPath( "myParent.myField" ) );
        field.setParentPath( new ConfigItemPath( "myNewParent" ) );
        assertEquals( "myNewParent.myField", field.getPath().toString() );

        field = Field.newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).build();
        field.setParentPath( new ConfigItemPath( "myGrandParent.myParent" ) );
        assertEquals( "myGrandParent.myParent.myField", field.getPath().toString() );

        field = Field.newField().name( "myField" ).type( FieldTypes.TEXT_LINE ).build();
        field.setPath( new ConfigItemPath( "myParent.myField" ) );
        field.setParentPath( new ConfigItemPath( "myNewGrandParent.myNewParent" ) );
        assertEquals( "myNewGrandParent.myNewParent.myField", field.getPath().toString() );
    }
}
