package com.enonic.xp.impl.macro;

import org.junit.Test;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;

import static org.junit.Assert.*;

public class ModifiedUserMacroProcessorTest
{
    @Test
    public void testProcess()
    {

        final MacroContext macroContext1 = MacroContext.create().name( "name" ).
            param( "modified_user", "User 1" ).
            build();

        final MacroProcessor macroProcessor = new ModifiedUserMacroProcessor();

        assertEquals( "User 1", macroProcessor.process( macroContext1 ) );

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).build();

        assertEquals( "unknown", macroProcessor.process( macroContext2 ) );
    }
}
