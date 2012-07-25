package com.enonic.wem.core.content.data;


import org.junit.Test;

import static org.junit.Assert.*;

public class ValuePathTest
{

    @Test
    public void tostring()
    {
        assertEquals( "car[0]", new ValuePath( "car[0]" ).toString() );
        assertEquals( "car[0].model", new ValuePath( "car[0].model" ).toString() );
    }

    @Test
    public void new_given_existing_fieldPath_and_name()
    {
        assertEquals( "car[0].model", new ValuePath( new ValuePath( "car[0]" ), "model" ).toString() );
    }

    @Test
    public void element_getPosition()
    {
        ValuePath.Element element = new ValuePath( "car[1]" ).iterator().next();
        assertEquals( 1, element.getPosition() );
        assertEquals( "car[1]", element.toString() );

        element = new ValuePath( "car" ).iterator().next();
        assertEquals( 0, element.getPosition() );
        assertEquals( "car", element.toString() );
    }

    @Test
    public void resolveFieldPath()
    {
        assertEquals( "car", new ValuePath( "car[0]" ).resolveFieldPath().toString() );
        assertEquals( "car.model", new ValuePath( "car[0].model" ).resolveFieldPath().toString() );
    }

    @Test
    public void startsWith()
    {
        assertTrue( new ValuePath( "car" ).startsWith( new ValuePath( "car" ) ) );
        assertTrue( new ValuePath( "car[0].model" ).startsWith( new ValuePath( "car[0]" ) ) );
        assertTrue( new ValuePath( "car[0].model" ).startsWith( new ValuePath( "car[0].model" ) ) );
        assertTrue( new ValuePath( "car[0].model.other" ).startsWith( new ValuePath( "car[0].model" ) ) );

        assertFalse( new ValuePath( "car" ).startsWith( new ValuePath( "bicycle" ) ) );
        assertFalse( new ValuePath( "car[0].model" ).startsWith( new ValuePath( "bicycle[0].model" ) ) );
        assertFalse( new ValuePath( "car[0]" ).startsWith( new ValuePath( "car[0].model" ) ) );
        assertFalse( new ValuePath( "car[0].model" ).startsWith( new ValuePath( "car[0].year" ) ) );
    }
}
