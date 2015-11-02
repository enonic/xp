package com.enonic.xp.script.runtime;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScriptSettingsTest
{
    @Test
    public void testEmpty()
    {
        final ScriptSettings settings = ScriptSettings.create().build();

        assertEquals( "", settings.getBasePath() );
        assertNull( settings.getBinding( String.class ) );
    }

    @Test
    public void testBasePath()
    {
        final ScriptSettings settings = ScriptSettings.create().
            basePath( "/site" ).
            build();

        assertEquals( "/site", settings.getBasePath() );
    }

    @Test
    public void testAttributes()
    {
        final ScriptSettings settings = ScriptSettings.create().
            binding( String.class, () -> "hello" ).
            binding( Integer.class, () -> 2 ).
            build();

        assertNotNull( settings.getBinding( String.class ) );
        assertEquals( "hello", settings.getBinding( String.class ).get() );

        assertNotNull( settings.getBinding( Integer.class ) );
        assertEquals( new Integer( 2 ), settings.getBinding( Integer.class ).get() );
    }
}
