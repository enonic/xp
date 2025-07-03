package com.enonic.xp.macro;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        final MacroDescriptors macroDescriptors1 = MacroDescriptors.from( MacroDescriptor.create().key( "my-app:macro1" ).build() );
        assertEquals( 1, macroDescriptors1.getSize() );
        assertEquals( "my-app:/site/macros/macro1/macro1.js", macroDescriptors1.iterator().next().toControllerResourceKey().toString() );
    }

}
