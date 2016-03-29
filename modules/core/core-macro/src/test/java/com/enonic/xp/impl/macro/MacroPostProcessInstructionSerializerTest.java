package com.enonic.xp.impl.macro;

import org.junit.Test;

import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroKey;

import static org.junit.Assert.*;

public class MacroPostProcessInstructionSerializerTest
{

    @Test
    public void testSerialize()
    {
        final MacroPostProcessInstructionSerializer serializer = new MacroPostProcessInstructionSerializer();

        final Macro macro1 = Macro.create().key( MacroKey.from( "my-app:macro" ) ).
            body( "body" ).
            param( "param1", "value1" ).
            param( "param2", "value2" ).
            build();

        final Macro macro2 = Macro.create().key( MacroKey.from( "my-app:macro2" ) ).
            body( "some !@#$%^&*() body" ).
            param( "param1", "!=dsfsf" ).
            param( "param2", "\"AAA\"--" ).
            build();

        final Macro macro3 = Macro.create().key( MacroKey.from( "my-app:macro" ) ).
            param( "param1", "value1" ).
            param( "param2", "value2" ).
            build();

        final Macro macro4 = Macro.create().key( MacroKey.from( "my-app:macro4" ) ).
            body( "some !@#$%^&*() \" -- body" ).
            build();

        assertEquals( "<!--#MACRO _name=\"macro\" param1=\"value1\" param2=\"value2\" _body=\"body\"-->", serializer.serialize( macro1 ) );

        assertEquals( "<!--#MACRO _name=\"macro2\" param1=\"!=dsfsf\" param2=\"\\\"AAA\\\"\\-\\-\" _body=\"some !@#$%^&*() body\"-->",
                      serializer.serialize( macro2 ) );

        assertEquals( "<!--#MACRO _name=\"macro\" param1=\"value1\" param2=\"value2\" _body=\"\"-->", serializer.serialize( macro3 ) );

        assertEquals( "<!--#MACRO _name=\"macro4\" _body=\"some !@#$%^&*() \\\" \\-\\- body\"-->", serializer.serialize( macro4 ) );
    }
}
