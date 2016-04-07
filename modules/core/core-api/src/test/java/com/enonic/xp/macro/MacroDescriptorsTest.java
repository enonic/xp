package com.enonic.xp.macro;

import org.junit.Test;

import static org.junit.Assert.*;

public class MacroDescriptorsTest
{

    @Test
    public void testEmpty()
    {
        final MacroDescriptors macroDescriptors = MacroDescriptors.empty();
        assertEquals( 0, macroDescriptors.getSize() );
    }

    @Test
    public void testFrom()
    {
        final MacroDescriptors macroDescriptors1 = MacroDescriptors.from( MacroDescriptor.from( "my-app:macro1" ) );
        assertEquals( 1, macroDescriptors1.getSize() );
        assertEquals( "my-app:/site/macros/macro1/macro1.js", macroDescriptors1.iterator().next().toResourceKey().toString() );

        final MacroDescriptors macroDescriptors2 = MacroDescriptors.from( "my-app:macro1", "my-app:macro2" );
        assertEquals( 2, macroDescriptors2.getSize() );

        final MacroDescriptors macroDescriptors3 = MacroDescriptors.from( macroDescriptors1 );
        assertEquals( 1, macroDescriptors3.getSize() );
    }

}
