package com.enonic.wem.api.content.data;


import org.junit.Assert;
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
    public void new_given_existing_componentPath_and_name()
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
    public void resolveComponentPath()
    {
        assertEquals( "car", new EntryPath( "car[0]" ).resolveComponentPath().toString() );
        assertEquals( "car.model", new EntryPath( "car[0].model" ).resolveComponentPath().toString() );
    }

    @Test
    public void startsWith()
    {
        Assert.assertTrue( new EntryPath( "car" ).startsWith( new EntryPath( "car" ) ) );
        Assert.assertTrue( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "car[0]" ) ) );
        Assert.assertTrue( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "car[0].model" ) ) );
        Assert.assertTrue( new EntryPath( "car[0].model.other" ).startsWith( new EntryPath( "car[0].model" ) ) );

        Assert.assertFalse( new EntryPath( "car" ).startsWith( new EntryPath( "bicycle" ) ) );
        Assert.assertFalse( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "bicycle[0].model" ) ) );
        Assert.assertFalse( new EntryPath( "car[0]" ).startsWith( new EntryPath( "car[0].model" ) ) );
        Assert.assertFalse( new EntryPath( "car[0].model" ).startsWith( new EntryPath( "car[0].year" ) ) );
    }

    @Test
    public void asNewWithIndexAtPath()
    {
        assertEquals( "componentSet",
                      new EntryPath( "componentSet" ).asNewWithIndexAtPath( 0, new EntryPath( "nonExisting" ) ).toString() );

        assertEquals( "componentSet[0]",
                      new EntryPath( "componentSet" ).asNewWithIndexAtPath( 0, new EntryPath( "componentSet" ) ).toString() );
        assertEquals( "componentSet[0].input",
                      new EntryPath( "componentSet.input" ).asNewWithIndexAtPath( 0, new EntryPath( "componentSet" ) ).toString() );
        assertEquals( "anotherSet.componentSet[0].input", new EntryPath( "anotherSet.componentSet.input" ).asNewWithIndexAtPath( 0,
                                                                                                                                 new EntryPath(
                                                                                                                                     "anotherSet.componentSet" ) ).toString() );
        assertEquals( "anotherSet.componentSet.input[0]", new EntryPath( "anotherSet.componentSet.input" ).asNewWithIndexAtPath( 0,
                                                                                                                                 new EntryPath(
                                                                                                                                     "anotherSet.componentSet.input" ) ).toString() );

    }
}
