package com.enonic.xp;

import org.junit.Test;
import static org.junit.Assert.*;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.macro.MacroKey;

public class MacroKeyTest
{
    @Test
    public void testName() {
        final MacroKey macroKey = new MacroKey( ApplicationKey.from( "my-app" ), "macros1" );
        assertEquals( "my-app:macros1", macroKey.toString() );
    }

    @Test
    public void testFrom() {
        final MacroKey macroKey1 = MacroKey.from( ApplicationKey.from( "my-app" ), "macros1" );
        assertEquals( "my-app:macros1", macroKey1.toString() );

        final MacroKey macroKey2 = MacroKey.from( "my-app:macros1" );
        assertEquals( "my-app:macros1", macroKey2.toString() );
    }

    @Test
    public void testEquals() {
        final MacroKey macroKey1 = MacroKey.from( ApplicationKey.from( "my-app" ), "macros1" );
        final MacroKey macroKey2 = MacroKey.from( "my-app:macros1" );

        assertEquals( macroKey1, macroKey2 );
    }
}
