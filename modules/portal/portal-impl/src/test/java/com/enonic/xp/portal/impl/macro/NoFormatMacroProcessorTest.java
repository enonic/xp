package com.enonic.xp.portal.impl.macro;


import org.junit.Test;

import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;

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

        assertEquals( "here is macro: [macro]body[/macro]", processor.process( macroContext1 ).getBody() );

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).
            body( "<tagWithMacro>here is macro: [macro]body[/macro]</tagWithMacro>" ).
            build();

        assertEquals( "&lt;tagWithMacro&gt;here is macro: [macro]body[/macro]&lt;/tagWithMacro&gt;",
                      processor.process( macroContext2 ).getBody() );
    }
}
