package com.enonic.xp.macro;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MacroKeyTest
{
    @Test
    void testName()
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.from( "my-app" ), "macros1" );
        assertEquals( "my-app:macros1", macroKey.toString() );
    }

    @Test
    void testFrom()
    {
        final MacroKey macroKey1 = MacroKey.from( ApplicationKey.from( "my-app" ), "macros1" );
        assertEquals( "my-app:macros1", macroKey1.toString() );

        final MacroKey macroKey2 = MacroKey.from( "my-app:macros1" );
        assertEquals( "my-app:macros1", macroKey2.toString() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( MacroKey.class ).withNonnullFields( "applicationKey", "name" ).verify();
    }
}
