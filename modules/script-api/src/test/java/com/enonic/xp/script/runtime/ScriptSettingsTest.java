package com.enonic.xp.script.runtime;

import org.junit.Test;

import com.google.common.base.Joiner;

import static org.junit.Assert.*;

public class ScriptSettingsTest
{
    @Test
    public void testEmpty()
    {
        final ScriptSettings settings = ScriptSettings.create().build();

        assertNull( settings.getBasePath() );
        assertNotNull( settings.getGlobalVariables() );
        assertEquals( 0, settings.getGlobalVariables().size() );
        assertNull( settings.getAttribute( String.class ) );
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
    public void testGlobalVariable()
    {
        final ScriptSettings settings = ScriptSettings.create().
            globalVariable( "a", 1 ).
            globalVariable( "b", 2 ).
            build();

        assertNotNull( settings.getGlobalVariables() );
        assertEquals( 2, settings.getGlobalVariables().size() );
        assertEquals( "a=1,b=2", Joiner.on( "," ).withKeyValueSeparator( "=" ).join( settings.getGlobalVariables() ) );
    }

    @Test
    public void testAttributes()
    {
        final ScriptSettings settings = ScriptSettings.create().
            attribute( String.class, () -> "hello" ).
            attribute( Integer.class, () -> 2 ).
            build();

        assertNotNull( settings.getAttribute( String.class ) );
        assertEquals( "hello", settings.getAttribute( String.class ).get() );

        assertNotNull( settings.getAttribute( Integer.class ) );
        assertEquals( new Integer( 2 ), settings.getAttribute( Integer.class ).get() );
    }
}
