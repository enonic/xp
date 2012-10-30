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
        assertEquals( "formItemSet", new EntryPath( "formItemSet" ).asNewWithIndexAtPath( 0, new EntryPath( "nonExisting" ) ).toString() );

        assertEquals( "formItemSet[0]",
                      new EntryPath( "formItemSet" ).asNewWithIndexAtPath( 0, new EntryPath( "formItemSet" ) ).toString() );
        assertEquals( "formItemSet[0].input",
                      new EntryPath( "formItemSet.input" ).asNewWithIndexAtPath( 0, new EntryPath( "formItemSet" ) ).toString() );
        assertEquals( "anotherSet.formItemSet[0].input", new EntryPath( "anotherSet.formItemSet.input" ).asNewWithIndexAtPath( 0,
                                                                                                                               new EntryPath(
                                                                                                                                   "anotherSet.formItemSet" ) ).toString() );
        assertEquals( "anotherSet.formItemSet.input[0]", new EntryPath( "anotherSet.formItemSet.input" ).asNewWithIndexAtPath( 0,
                                                                                                                               new EntryPath(
                                                                                                                                   "anotherSet.formItemSet.input" ) ).toString() );

    }
}
