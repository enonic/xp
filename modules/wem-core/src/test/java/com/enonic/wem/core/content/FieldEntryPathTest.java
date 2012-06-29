package com.enonic.wem.core.content;


import org.junit.Test;

import static org.junit.Assert.*;

public class FieldEntryPathTest
{

    @Test
    public void tostring()
    {
        assertEquals( "car[0]", new FieldEntryPath( "car[0]" ).toString() );
        assertEquals( "car[0].model", new FieldEntryPath( "car[0].model" ).toString() );
    }

    @Test
    public void new_given_existing_fieldPath_and_name()
    {
        assertEquals( "car[0].model", new FieldEntryPath( new FieldEntryPath( "car[0]" ), "model" ).toString() );
    }

    @Test
    public void element_getPosition()
    {
        FieldEntryPath.Element element = new FieldEntryPath( "car[1]" ).iterator().next();
        assertEquals( 1, element.getPosition() );
        assertEquals( "car[1]", element.toString() );

        element = new FieldEntryPath( "car" ).iterator().next();
        assertEquals( 0, element.getPosition() );
        assertEquals( "car", element.toString() );
    }

    @Test
    public void resolveFieldPath()
    {
        assertEquals( "car", new FieldEntryPath( "car[0]" ).resolveFieldPath().toString() );
        assertEquals( "car.model", new FieldEntryPath( "car[0].model" ).resolveFieldPath().toString() );
    }
}
