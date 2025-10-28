package com.enonic.xp.portal.impl.macro;


import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DisableMacroProcessorTest
{

    @Test
    void testProcess()
    {
        final MacroProcessor processor = new DisableMacroProcessor();

        final MacroContext macroContext1 = MacroContext.create().name( "name" ).
            body( "here is macro: [macro]body[/macro]" ).
            build();

        assertEquals( "here is macro: [macro]body[/macro]", processor.process( macroContext1 ).getBody() );

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).
            body( "<tagWithMacro>here is macro: [macro]body[/macro]</tagWithMacro>" ).
            build();

        assertEquals( "<tagWithMacro>here is macro: [macro]body[/macro]</tagWithMacro>", processor.process( macroContext2 ).getBody() );
    }
}
