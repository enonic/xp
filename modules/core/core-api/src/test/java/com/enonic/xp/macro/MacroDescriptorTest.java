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

    @Test
    public void testFrom()
    {
        final MacroDescriptor macroDescriptor1 = MacroDescriptor.from( "my-app:macro1" );
        assertEquals( "my-app:/site/macros/macro1/macro1.xml", macroDescriptor1.toResourceKey().toString() );

        final MacroDescriptor macroDescriptor2 = MacroDescriptor.from( MacroKey.from( "my-app:macro2" ) );
        assertEquals( "my-app:/site/macros/macro2/macro2.xml", macroDescriptor2.toResourceKey().toString() );
    }
}
