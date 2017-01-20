package com.enonic.xp;

import org.junit.Test;

import static org.junit.Assert.*;

public class JavaVersionTest
{
    @Test
    public void testIfRightVersion()
    {
        final String value = System.getProperty( "java.version" );
        assertTrue( getVersion( value ).startsWith( "1.8" ) );
        assertTrue( getUpdate( value ) >= 92 );
    }

    private int getUpdate( final String value )
    {
        final String version = getVersion( value );
        final int index = version.indexOf( '_' );
        if ( index <= 0 )
        {
            return 0;
        }

        try
        {
            return Integer.parseInt( version.substring( index + 1 ) );
        }
        catch ( final Exception e )
        {
            return 0;
        }
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
