package com.enonic.xp.portal.impl.macro;

import org.junit.Test;

import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;

import static org.junit.Assert.*;

public class TwitterMacroProcessorTest
{
    @Test
    public void testProcess()
    {

        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "http://twitter-url/bla" ).
            param( "lang", "nw" ).
            build();

        final MacroProcessor macroProcessor = new TwitterMacroProcessor();

        assertEquals(
            "<blockquote class=\"twitter-tweet\" lang=\"nw\"><a hidden=\"true\" href=\"http://twitter-url/bla\">Link to tweet</a></blockquote>\n" +
                "<script src=\"//platform.twitter.com/widgets.js\" async=\"\" charset=\"utf-8\">",
            macroProcessor.process( macroContext ).getBody() );

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "http://twitter-url/bla2" ).
            build();

        assertEquals(
            "<blockquote class=\"twitter-tweet\" lang=\"en\"><a hidden=\"true\" href=\"http://twitter-url/bla2\">Link to tweet</a></blockquote>\n" +
                "<script src=\"//platform.twitter.com/widgets.js\" async=\"\" charset=\"utf-8\">",
            macroProcessor.process( macroContext2 ).getBody() );
    }
}
