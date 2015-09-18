package com.enonic.xp.launcher.env;

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
    public void testIsJava8()
    {
        assertEquals( false, newVersion( "1.7.0" ).isJava8() );
        assertEquals( true, newVersion( "1.8.0_40" ).isJava8() );
    }

    @Test
    public void testUpdates()
    {
        assertEquals( 40, newVersion( "1.8.0_40" ).getUpdate() );
        assertEquals( 45, newVersion( "1.8.0_45" ).getUpdate() );
        assertEquals( 45, newVersion( "1.8.0_45-internal" ).getUpdate() );
    }
}
