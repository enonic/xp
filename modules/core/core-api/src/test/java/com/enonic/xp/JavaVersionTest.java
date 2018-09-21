package com.enonic.xp;

import org.junit.Test;

import static org.junit.Assert.*;

public class JavaVersionTest
{
    @Test
    public void testIfRightVersion()
    {
        final String value = System.getProperty( "java.version" );
        assertTrue( getVersion( value ).startsWith( "10." ) );
    }

    private String getVersion( final String value )
    {
        final int index = value.indexOf( '-' );
        if ( index <= 0 )
        {
            return value;
        }
        else
        {
            return value.substring( 0, index );
        }
    }
}
