package com.enonic.xp.portal.impl.macro;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.url.PortalUrlService;

import static com.enonic.xp.portal.impl.macro.YoutubeMacroProcessor.COMMON_STYLE_ASSET_PATH;
import static com.enonic.xp.portal.impl.macro.YoutubeMacroProcessor.SYSTEM_APPLICATION_KEY;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class YoutubeMacroProcessorTest
{
    private YoutubeMacroProcessor macroProcessor;

    @Before
    public void setup()
        throws Exception
    {
        final PortalUrlService portalUrlService = Mockito.mock( PortalUrlService.class );
        final String url = "/admin/portal/preview/draft/_/asset/" + SYSTEM_APPLICATION_KEY + ":1464071104/" + COMMON_STYLE_ASSET_PATH;
        Mockito.when( portalUrlService.assetUrl( any() ) ).thenReturn( url );

        macroProcessor = new YoutubeMacroProcessor();
        macroProcessor.setPortalUrlService( portalUrlService );
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
    public void testProcessUrlHasCommmonAssetRef()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "url", "http://www.youtube.com/v/gdfgdfg?version=3&autohide=1" ).
            build();

        assertEquals( "<div class='youtube-video-wrapper'><iframe src='https://www.youtube.com/embed/gdfgdfg'></iframe></div>",
                      macroProcessor.process( macroContext ).getBody() );

        assertEquals(
            "<link rel='stylesheet' type='text/css' href='/admin/portal/preview/draft/_/asset/com.enonic.xp.app.system:1464071104/css/macro/youtube/youtube.css'/>",
            macroProcessor.process( macroContext ).getContributions( HtmlTag.HEAD_BEGIN ).get( 0 ) );
    }
}
