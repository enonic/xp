package com.enonic.xp.portal.impl.rendering;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.url.PortalUrlServiceImpl;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TextRendererTest
{
    private PortalRequest portalRequest;

    private PortalResponse portalResponse;

    private TextComponent textComponent;

    private TextRenderer renderer;

    private PortalUrlService service;

    @BeforeEach
    public void before()
    {
        portalRequest = new PortalRequest();
        portalResponse = PortalResponse.create().build();
        PortalUrlServiceImpl portalUrlService = new PortalUrlServiceImpl();
        portalUrlService.setMacroService( new MockMacroService() );
        portalUrlService.setStyleDescriptorService( new MockStyleDescriptorService() );
        service = portalUrlService;
        portalRequest.setMode( RenderMode.LIVE );
    }

    @Test
    public void textComponentWithNoText()
    {
        // setup
        textComponent = TextComponent.create().build();
        renderer = new TextRenderer();
        renderer.setPortalUrlService( service );

        // exercise
        portalResponse = renderer.render( textComponent, portalRequest );

        // verify
        assertEquals( "", portalResponse.getAsString() );
    }

    @Test
    public void textComponentWithNoTextAndRenderModeEdit()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        textComponent = TextComponent.create().build();
        renderer = new TextRenderer();
        renderer.setPortalUrlService( service );

        // exercise
        portalResponse = renderer.render( textComponent, portalRequest );
        // verify
        assertEquals( "<div data-portal-component-type=\"text\"><section></section></div>", portalResponse.getAsString() );
    }

    @Test
    public void textComponentWithNoTextAndRenderModePreview()
    {
        // setup
        portalRequest.setMode( RenderMode.PREVIEW );
        textComponent = TextComponent.create().build();
        renderer = new TextRenderer();
        renderer.setPortalUrlService( service );

        // exercise
        portalResponse = renderer.render( textComponent, portalRequest );

        // verify
        assertEquals( "<section data-portal-component-type=\"text\"></section>", portalResponse.getAsString() );
    }

    @Test
    public void textComponentWithNoTextAndRenderModeInline()
    {
        // setup
        portalRequest.setMode( RenderMode.INLINE );
        textComponent = TextComponent.create().build();
        renderer = new TextRenderer();
        renderer.setPortalUrlService( service );

        // exercise
        portalResponse = renderer.render( textComponent, portalRequest );

        // verify
        assertEquals( "<section data-portal-component-type=\"text\"></section>", portalResponse.getAsString() );
    }


    @Test
    public void textComponentWithSomeTextAndRenderModePreview()
    {
        // setup
        String text = "<h2>hello</h2><p>How are you?</p>";
        textComponent = TextComponent.create().text( text ).build();
        renderer = new TextRenderer();
        renderer.setPortalUrlService( service );

        // exercise
        portalResponse = renderer.render( textComponent, portalRequest );

        // verify
        assertEquals( "<section data-portal-component-type=\"text\">" + text + "</section>", portalResponse.getAsString() );
    }

    @Test
    public void textComponentRendererRemovesEmptyFigCaptionTags()
    {
        // setup
        String text = "<figure><img src=\"src\" />\n" + "<figcaption style=\"text-align: left;\"></figcaption>\n" + "</figure>";
        textComponent = TextComponent.create().text( text ).build();
        renderer = new TextRenderer();
        renderer.setPortalUrlService( service );

        // exercise
        portalResponse = renderer.render( textComponent, portalRequest );

        // verify
        assertFalse( portalResponse.getAsString().contains( "figcaption" ) );
    }

    @Test
    public void textComponentWithSomeTextAndRenderModeEdit()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        String text = "<h2>hello</h2><p>How are you?</p>";
        textComponent = TextComponent.create().text( text ).build();
        renderer = new TextRenderer();
        renderer.setPortalUrlService( service );

        // exercise
        portalResponse = renderer.render( textComponent, portalRequest );

        // verify
        assertEquals( "<div data-portal-component-type=\"text\"><section>" + text + "</section></div>", portalResponse.getAsString() );
    }

    private static class MockMacroService
        implements MacroService
    {
        @Override
        public Macro parse( final String text )
        {
            return null;
        }

        @Override
        public String evaluateMacros( final String text, final Function<Macro, String> macroProcessor )
        {
            return text;
        }

        @Override
        public String postProcessInstructionSerialize( final Macro macro )
        {
            return null;
        }
    }

    private static class MockStyleDescriptorService
        implements StyleDescriptorService
    {
        @Override
        public StyleDescriptor getByApplication( final ApplicationKey key )
        {
            return null;
        }

        @Override
        public StyleDescriptors getByApplications( final ApplicationKeys applicationKeys )
        {
            return StyleDescriptors.empty();
        }

        @Override
        public StyleDescriptors getAll()
        {
            return null;
        }
    }
}
