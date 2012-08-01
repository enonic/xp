package com.enonic.wem.core.content.data;


import org.junit.Test;

import static org.junit.Assert.*;

public class EntryPathTest
{

    @Test
    public void tostring()
    {
        assertEquals( "car[0]", new EntryPath( "car[0]" ).toString() );
        assertEquals( "car[0].model", new EntryPath( "car[0].model" ).toString() );
    }

    @Test
    public void new_given_existing_configItemPath_and_name()
    {
        assertEquals( "car[0].model", new EntryPath( new EntryPath( "car[0]" ), "model" ).toString() );
    }

    @Test
    public void element_getPosition()
    {
        EntryPath.Element element = new EntryPath( "car[1]" ).iterator().next();
        assertEquals( 1, element.getPosition() );
        assertEquals( "car[1]", element.toString() );

        element = new EntryPath( "car" ).iterator().next();
        assertEquals( 0, element.getPosition() );
        assertEquals( "car", element.toString() );
    }

    @Test
    public void resolveConfigItemPath()
    {
        assertEquals( "car", new EntryPath( "car[0]" ).resolveConfigItemPath().toString() );
        assertEquals( "car.model", new EntryPath( "car[0].model" ).resolveConfigItemPath().toString() );
    }

    @Test
    public void startsWith()
    {
        assertTrue( new EntryPath( "car" ).startsWith( new EntryPath( "car" ) ) );
        assertTrue( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "car[0]" ) ) );
        assertTrue( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "car[0].model" ) ) );
        assertTrue( new EntryPath( "car[0].model.other" ).startsWith( new EntryPath( "car[0].model" ) ) );

        assertFalse( new EntryPath( "car" ).startsWith( new EntryPath( "bicycle" ) ) );
        assertFalse( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "bicycle[0].model" ) ) );
        assertFalse( new EntryPath( "car[0]" ).startsWith( new EntryPath( "car[0].model" ) ) );
        assertFalse( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "car[0].year" ) ) );
    }
}
