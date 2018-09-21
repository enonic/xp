package com.enonic.xp.launcher.impl.env;

import org.junit.Test;

import static org.junit.Assert.*;

public class JavaVersionTest
{
    private JavaVersion newVersion( final String value )
    {
        final SystemProperties props = new SystemProperties();
        props.put( "java.version", value );
        return new JavaVersion( props );
    }

    @Test
    public void testIsJava10()
    {
        assertTrue( newVersion( "10.0.2" ).isJava10() );
        assertFalse( newVersion( "1.7.0" ).isJava10() );
        assertFalse( newVersion( "1.8.0_40" ).isJava10() );
    }

    @Test
    public void testUpdates()
    {
        assertEquals( 0, newVersion( "1.8.0" ).getUpdate() );
        assertEquals( 0, newVersion( "1.8.0_unknown" ).getUpdate() );
        assertEquals( 40, newVersion( "1.8.0_40" ).getUpdate() );
        assertEquals( 45, newVersion( "1.8.0_45" ).getUpdate() );
        assertEquals( 45, newVersion( "1.8.0_45-internal" ).getUpdate() );
    }
}
