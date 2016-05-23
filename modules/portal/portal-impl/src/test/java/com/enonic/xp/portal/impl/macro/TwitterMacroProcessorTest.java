package com.enonic.xp.portal.impl.macro;

import org.junit.Test;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.postprocess.HtmlTag;

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

        PortalResponse response = macroProcessor.process( macroContext );

        assertEquals(
            "<blockquote class=\"twitter-tweet\" lang=\"nw\"><a hidden=\"true\" href=\"http://twitter-url/bla\">Link to tweet</a></blockquote>\n",
            response.getBody() );
        assertEquals( "<script src=\"//platform.twitter.com/widgets.js\" async=\"\" charset=\"utf-8\"></script>",
                      response.getContributions( HtmlTag.BODY_END ).get( 0 ) );

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "http://twitter-url/bla2" ).
            build();

        response = macroProcessor.process( macroContext2 );

        assertEquals(
            "<blockquote class=\"twitter-tweet\" lang=\"en\"><a hidden=\"true\" href=\"http://twitter-url/bla2\">Link to tweet</a></blockquote>\n",
            response.getBody() );
        assertEquals( "<script src=\"//platform.twitter.com/widgets.js\" async=\"\" charset=\"utf-8\"></script>",
                      response.getContributions( HtmlTag.BODY_END ).get( 0 ) );
    }
}
