package com.enonic.xp.app;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationKeysTest
{
    private static final ArrayList<ApplicationKey> list = new ArrayList<>();

    @BeforeAll
    public static void initApplicationKeys()
    {
        ApplicationKeysTest.list.add( ApplicationKey.from( "aaa" ) );
        ApplicationKeysTest.list.add( ApplicationKey.from( "bbb" ) );
        ApplicationKeysTest.list.add( ApplicationKey.from( "ccc" ) );
    }

    @Test
    void empty()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.empty();

        assertEquals( 0, applicationKeys.getSize() );
    }

    @Test
    void fromArray()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.from( ApplicationKeysTest.list.get( 0 ), ApplicationKeysTest.list.get( 1 ),
                                                                ApplicationKeysTest.list.get( 2 ) );

        assertEquals( 3, applicationKeys.getSize() );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }

    @Test
    void fromIterable()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.from( (Iterable<ApplicationKey>) ApplicationKeysTest.list );

        assertEquals( 3, applicationKeys.getSize() );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }

    @Test
    void fromCollection()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.from( ApplicationKeysTest.list );

        assertEquals( 3, applicationKeys.getSize() );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }

    @Test
    void fromStringArray()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.from( "aaa", "bbb", "ccc" );

        assertEquals( 3, applicationKeys.getSize() );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( applicationKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }

    @Test
    void deduplicate()
    {
        ApplicationKeys applicationKeys =
            ApplicationKeys.from( ApplicationKey.from( "aaa" ), ApplicationKey.from( "aaa" ), ApplicationKey.from( "bbb" ) );

        assertThat( applicationKeys ).containsExactly( ApplicationKey.from( "aaa" ), ApplicationKey.from( "bbb" ) );
    }

    @Test
    void empty_from_same()
    {
        assertSame( ApplicationKeys.empty(), ApplicationKeys.from( Collections.emptyList() ) );
    }
}
