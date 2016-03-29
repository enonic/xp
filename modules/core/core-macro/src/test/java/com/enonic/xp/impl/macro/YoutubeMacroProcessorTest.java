package com.enonic.xp.impl.macro;

import org.junit.Test;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;

import static org.junit.Assert.*;

public class YoutubeMacroProcessorTest
{
    @Test
    public void testProcess()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "https://www.youtube.com/watch?v=iDdDd" ).
            param( "width", "200" ).
            param( "height", "100" ).
            build();

        final MacroProcessor macroProcessor = new YoutubeMacroProcessor();

        assertEquals( "<iframe width=\"200\" height=\"100\" src=\"https://www.youtube.com/watch?v=iDdDd\"></iframe>",
                      macroProcessor.process( macroContext ) );

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "https://www.youtube.com/watch?v=iDdDd" ).
            param( "height", "100" ).
            build();

        assertEquals( "<iframe width=\"\" height=\"100\" src=\"https://www.youtube.com/watch?v=iDdDd\"></iframe>",
                      macroProcessor.process( macroContext2 ) );

        final MacroContext macroContext3 = MacroContext.create().name( "name" ).
            build();

        assertEquals( null, macroProcessor.process( macroContext3 ) );
    }
}
