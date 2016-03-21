package com.enonic.xp.macro;

import org.junit.Test;

import static org.junit.Assert.*;

public class MacroDescriptorTest
{
    @Test
    public void testToResourceKey()
    {
        final MacroDescriptor macroDescriptor = MacroDescriptor.create().key( MacroKey.from( "my-app:macro1" ) ).build();
        assertEquals( "my-app:/site/macros/macro1/macro1.xml", macroDescriptor.toResourceKey().toString() );
    }
}
