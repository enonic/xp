package com.enonic.xp.app;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationKeysTest
{
    private static final ApplicationKey[] APPS = {ApplicationKey.from( "aaa" ), ApplicationKey.from( "bbb" ), ApplicationKey.from( "ccc" )};

    @Test
    public void empty()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.empty();

        assertEquals( 0, applicationKeys.getSize() );
    }

    @Test
    public void fromArray()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.from( APPS[0], APPS[1], APPS[2] );

        assertEquals( 3, applicationKeys.getSize() );
        assertTrue( applicationKeys.contains( APPS[0] ) );
        assertTrue( applicationKeys.contains( APPS[1] ) );
        assertTrue( applicationKeys.contains( APPS[2] ) );
    }

    @Test
    public void fromIterable()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.from( Arrays.asList( APPS ) );

        assertEquals( 3, applicationKeys.getSize() );
        assertTrue( applicationKeys.contains( APPS[0] ) );
        assertTrue( applicationKeys.contains( APPS[1] ) );
        assertTrue( applicationKeys.contains( APPS[2] ) );
    }

    @Test
    public void fromStringArray()
    {
        ApplicationKeys applicationKeys = ApplicationKeys.from( "aaa", "bbb", "ccc" );

        assertEquals( 3, applicationKeys.getSize() );
        assertTrue( applicationKeys.contains( APPS[0] ) );
        assertTrue( applicationKeys.contains( APPS[1] ) );
        assertTrue( applicationKeys.contains( APPS[2] ) );
    }
}