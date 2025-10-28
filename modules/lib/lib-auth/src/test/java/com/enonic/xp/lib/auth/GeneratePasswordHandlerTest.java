package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;

import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratePasswordHandlerTest
    extends ScriptTestSupport
{
    @Test
    void testExamples()
    {
        runScript( "/lib/xp/examples/auth/generatePassword.js" );
    }

    @Test
    void testPasswordGeneration()
    {
        for ( int i = 0; i < 20; i++ )
        {
            assertTrue( isExtreme( new GeneratePasswordHandler().generatePassword() ) );
        }
    }

    @Test
    void testFunction()
    {
        runFunction( "/test/generatePassword-test.js", "generatePassword" );
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
