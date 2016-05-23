package com.enonic.xp.portal.impl.macro;

import org.junit.Test;

import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.postprocess.HtmlTag;

import static org.junit.Assert.*;

public class YoutubeMacroProcessorTest
{
    final MacroProcessor macroProcessor = new YoutubeMacroProcessor();

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

        assertEquals( "<div class='youtube-video-wrapper'><iframe src='https://www.youtube.com/embed/iDdDd'></iframe></div>",
                      macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrl2()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "https://www.youtube.com/watch?v=iDdDd" ).
            build();

        assertEquals( "<div class='youtube-video-wrapper'><iframe src='https://www.youtube.com/embed/iDdDd'></iframe></div>",
                      macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrlWithParams()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "https://www.youtube.com/watch?someParam=xXx&v=iDdDd&t=25" ).
            build();

        assertEquals( "<div class='youtube-video-wrapper'><iframe src='https://www.youtube.com/embed/iDdDd'></iframe></div>",
                      macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrl3()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "http://youtu.be/cFfxuWUgcvI" ).
            build();

        assertEquals( "<div class='youtube-video-wrapper'><iframe src='https://www.youtube.com/embed/cFfxuWUgcvI'></iframe></div>",
                      macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrl4()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "http://www.youtube.com/v/gdfgdfg" ).
            build();

        assertEquals( "<div class='youtube-video-wrapper'><iframe src='https://www.youtube.com/embed/gdfgdfg'></iframe></div>",
                      macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrl5()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "http://www.youtube.com/v/gdfgdfg?version=3&autohide=1" ).
            build();

        assertEquals( "<div class='youtube-video-wrapper'><iframe src='https://www.youtube.com/embed/gdfgdfg'></iframe></div>",
                      macroProcessor.process( macroContext ).getBody() );
    }

    @Test
    public void testProcessUrlHasCommmonStyleRef()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "http://www.youtube.com/v/gdfgdfg?version=3&autohide=1" ).
            systemAssetsBaseUri( "/portal/draft/_/asset/system:31556889864403199" ).
            build();

        assertEquals( "<div class='youtube-video-wrapper'><iframe src='https://www.youtube.com/embed/gdfgdfg'></iframe></div>",
                      macroProcessor.process( macroContext ).getBody() );

        assertEquals(
            "<link rel='stylesheet' type='text/css' href='/portal/draft/_/asset/system:31556889864403199/css/macro/youtube/youtube.css'/>",
            macroProcessor.process( macroContext ).getContributions( HtmlTag.HEAD_BEGIN ).get( 0 ) );
    }
}
