package com.enonic.xp.script.runtime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ScriptSettingsTest
{
    @Test
    public void testEmpty()
    {
        final ScriptSettings settings = ScriptSettings.create().build();
        assertNull( settings.getBinding( String.class ) );
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

    @Test
    public void testGlobals()
    {
        final ScriptSettings settings = ScriptSettings.create().
            globalVariable( "var", "hello" ).
            build();

        assertNotNull( settings.getGlobalVariables() );
        assertEquals( "hello", settings.getGlobalVariables().get( "var" ) );
    }
}
