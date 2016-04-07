package com.enonic.xp.portal.impl.macro;

import org.junit.Test;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;

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

        assertEquals( "<iframe src=\"https://www.youtube.com/watch?v=iDdDd\" width=\"200\" height=\"100\"></iframe>",
                      macroProcessor.process( macroContext ).getBody() );

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "https://www.youtube.com/watch?v=iDdDd" ).
            param( "height", "100" ).
            build();

        assertEquals( "<iframe src=\"https://www.youtube.com/watch?v=iDdDd\" height=\"100\"></iframe>",
                      macroProcessor.process( macroContext2 ).getBody() );

        final MacroContext macroContext3 = MacroContext.create().name( "name" ).
            build();

        assertEquals( null, macroProcessor.process( macroContext3 ) );

        final MacroContext macroContext4 = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "https://www.youtube.com/watch?v=iDdDd" ).
            build();

        assertEquals( "<iframe src=\"https://www.youtube.com/watch?v=iDdDd\"></iframe>",
                      macroProcessor.process( macroContext4 ).getBody() );
    }
}
