package com.enonic.xp.script.runtime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ScriptSettingsTest
{
    @Test
    void testEmpty()
    {
        final ScriptSettings settings = ScriptSettings.create().build();
        assertNull( settings.getBinding( String.class ) );
    }

    @Test
    void testAttributes()
    {
        final ScriptSettings settings = ScriptSettings.create().
            binding( String.class, () -> "hello" ).
            binding( Integer.class, () -> 2 ).
            build();

        assertNotNull( settings.getBinding( String.class ) );
        assertEquals( "hello", settings.getBinding( String.class ).get() );

        assertNotNull( settings.getBinding( Integer.class ) );
        assertEquals( 2, settings.getBinding( Integer.class ).get() );
    }

    @Test
    void testGlobals()
    {
        final ScriptSettings settings = ScriptSettings.create().
            globalVariable( "var", "hello" ).
            build();

        assertNotNull( settings.getGlobalVariables() );
        assertEquals( "hello", settings.getGlobalVariables().get( "var" ) );
    }
}
