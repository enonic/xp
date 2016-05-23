package com.enonic.xp.portal.impl.macro;

import org.junit.Test;

import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;

import static org.junit.Assert.*;

public class YoutubeMacroProcessorTest
{
    final MacroProcessor macroProcessor = new YoutubeMacroProcessor();

    @Test
    public void testProcessUrlAndHeight()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "https://www.youtube.com/watch?v=iDdDd" ).
            param( "height", "100" ).
            build();

        assertEquals( "<iframe src=\"https://www.youtube.com/embed/iDdDd\" height=\"100\"></iframe>",
                      macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessWithMissingUrl()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            build();

        assertEquals( null, macroProcessor.process( macroContext ) );
    }

    @Test
    public void testProcessUrl1()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "https://www.youtube.com/watch?v=iDdDd" ).
            build();

        assertEquals( "<iframe src=\"https://www.youtube.com/embed/iDdDd\"></iframe>", macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrl2()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "https://www.youtube.com/watch?v=iDdDd" ).
            build();

        assertEquals( "<iframe src=\"https://www.youtube.com/embed/iDdDd\"></iframe>", macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrlWithParams()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "https://www.youtube.com/watch?someParam=xXx&v=iDdDd&t=25" ).
            build();

        assertEquals( "<iframe src=\"https://www.youtube.com/embed/iDdDd\"></iframe>", macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrl3()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "http://youtu.be/cFfxuWUgcvI" ).
            build();

        assertEquals( "<iframe src=\"https://www.youtube.com/embed/cFfxuWUgcvI\"></iframe>",
                      macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrl4()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "http://www.youtube.com/v/gdfgdfg" ).
            build();

        assertEquals( "<iframe src=\"https://www.youtube.com/embed/gdfgdfg\"></iframe>", macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrl5()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "http://www.youtube.com/v/gdfgdfg?version=3&autohide=1" ).
            build();

        assertEquals( "<iframe src=\"https://www.youtube.com/embed/gdfgdfg\"></iframe>", macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrlWithHeightAndWidth()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "https://www.youtube.com/watch?v=iDdDd" ).
            param( "width", "200" ).
            param( "height", "100" ).
            build();

        assertEquals( "<iframe src=\"https://www.youtube.com/embed/iDdDd\" width=\"200\" height=\"100\"></iframe>",
                      macroProcessor.process( macroContext ).getBody() );
    }
}
