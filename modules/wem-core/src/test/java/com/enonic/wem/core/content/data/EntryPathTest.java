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
    public void new_given_existing_formItemPath_and_name()
    {
        assertEquals( "car[0].model", new EntryPath( new EntryPath( "car[0]" ), "model" ).toString() );
    }

    @Test
    public void element_getPosition()
    {
        EntryPath.Element element = new EntryPath( "car[1]" ).iterator().next();
        assertEquals( 1, element.getIndex() );
        assertEquals( "car[1]", element.toString() );

        element = new EntryPath( "car" ).iterator().next();
        assertEquals( 0, element.getIndex() );
        assertEquals( "car", element.toString() );
    }

    @Test
    public void resolveFormItemPath()
    {
        assertEquals( "car", new EntryPath( "car[0]" ).resolveFormItemPath().toString() );
        assertEquals( "car.model", new EntryPath( "car[0].model" ).resolveFormItemPath().toString() );
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

    @Test
    public void asNewWithIndexAtPath()
    {
        assertEquals( "formItemSet", new EntryPath( "formItemSet" ).asNewWithIndexAtPath( 0, new EntryPath( "nonExisting" ) ).toString() );

        assertEquals( "formItemSet[0]",
                      new EntryPath( "formItemSet" ).asNewWithIndexAtPath( 0, new EntryPath( "formItemSet" ) ).toString() );
        assertEquals( "formItemSet[0].component",
                      new EntryPath( "formItemSet.component" ).asNewWithIndexAtPath( 0, new EntryPath( "formItemSet" ) ).toString() );
        assertEquals( "anotherSet.formItemSet[0].component", new EntryPath( "anotherSet.formItemSet.component" ).asNewWithIndexAtPath( 0,
                                                                                                                                       new EntryPath(
                                                                                                                                           "anotherSet.formItemSet" ) ).toString() );
        assertEquals( "anotherSet.formItemSet.component[0]", new EntryPath( "anotherSet.formItemSet.component" ).asNewWithIndexAtPath( 0,
                                                                                                                                       new EntryPath(
                                                                                                                                           "anotherSet.formItemSet.component" ) ).toString() );

    }
}
