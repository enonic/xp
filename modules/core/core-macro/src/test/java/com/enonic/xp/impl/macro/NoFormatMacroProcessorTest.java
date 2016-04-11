package com.enonic.xp.impl.macro;


import org.junit.Test;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;

import static org.junit.Assert.*;

public class NoFormatMacroProcessorTest
{

    @Test
    public void testProcess()
    {
        final MacroProcessor processor = new NoFormatMacroProcessor();

        final MacroContext macroContext1 = MacroContext.create().name( "name" ).
            body( "here is macro: [macro]body[/macro]" ).
            build();

        assertEquals( "here is macro: [macro]body[/macro]", processor.process( macroContext1 ) );

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).
            body( "<tagWithMacro>here is macro: [macro]body[/macro]</tagWithMacro>" ).
            build();

        assertEquals( "<tagWithMacro>here is macro: [macro]body[/macro]</tagWithMacro>", processor.process( macroContext2 ) );
    }
}
