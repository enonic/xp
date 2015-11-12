package com.enonic.xp.lib.auth;

import org.junit.Test;

import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.junit.Assert.*;

public class GeneratePasswordHandlerTest
    extends ScriptTestSupport
{
    @Test
    public void testPasswordGeneration()
        throws Exception
    {
        for ( int i = 0; i < 20; i++ )
        {
            assertTrue( isExtreme( new GeneratePasswordHandler().generatePassword() ) );
        }
    }

    @Test
    public void testFunction()
    {
        runFunction( "/site/test/generatePassword-test.js", "generatePassword" );
    }

    private boolean isExtreme( String value )
    {
        return value != null &&
            value.length() >= 14 &&
            this.isMixedCase( value ) &&
            this.containsDigits( value ) &&
            this.containsSpecialChars( value );
    }

    private boolean containsDigits( String value )
    {
        return value.matches( ".*\\d.*" );
    }

    private boolean containsSpecialChars( String value )
    {
        return value.matches( ".*[^a-zA-Z0-9\\s].*" );
    }

    private boolean isMixedCase( String value )
    {
        return !value.toUpperCase().equals( value ) && !value.toLowerCase().equals( value );
    }
}
