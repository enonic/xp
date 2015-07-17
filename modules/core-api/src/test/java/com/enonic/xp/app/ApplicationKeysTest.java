package com.enonic.xp.app;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationKeysTest
{
    private static ArrayList<ApplicationKey> list = new ArrayList();

    @BeforeClass
    public static void initApplicationKeys()
    {
        ApplicationKeysTest.list.add( ApplicationKey.from( "aaa" ) );
        ApplicationKeysTest.list.add( ApplicationKey.from( "bbb" ) );
        ApplicationKeysTest.list.add( ApplicationKey.from( "ccc" ) );
    }

    @Test
    public void empty()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.empty();

        assertEquals( 0, applicationKeys.getSize() );
    }

    @Test
    public void fromArray()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.from( ApplicationKeysTest.list.get( 0 ), ApplicationKeysTest.list.get( 1 ),
                                                                ApplicationKeysTest.list.get( 2 ) );

        assertEquals( 3, applicationKeys.getSize() );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }

    @Test
    public void fromIterable()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.from( (Iterable) ApplicationKeysTest.list );

        assertEquals( 3, applicationKeys.getSize() );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }

    @Test
    public void fromCollection()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.from( ApplicationKeysTest.list );

        assertEquals( 3, applicationKeys.getSize() );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }

    @Test
    public void fromStringArray()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.from( "aaa", "bbb", "ccc" );

        assertEquals( 3, applicationKeys.getSize() );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }
}