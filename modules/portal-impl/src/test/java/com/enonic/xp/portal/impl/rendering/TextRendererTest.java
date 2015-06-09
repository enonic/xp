package com.enonic.xp.portal.impl.rendering;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.content.page.region.TextComponent;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;

import static org.junit.Assert.*;

public class TextRendererTest
{
    private PortalRequest portalRequest;

    private PortalResponse portalResponse;

    private TextComponent textComponent;

    private TextRenderer renderer;

    @Before
    public void before()
    {
        portalRequest = new PortalRequest();
        portalResponse = PortalResponse.create().build();
        portalRequest.setMode( RenderMode.LIVE );
    }

    @Test
    public void textComponentWithNoText()
    {
        // setup
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).build();
        renderer = new TextRenderer();

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
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).build();
        renderer = new TextRenderer();

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
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).build();
        renderer = new TextRenderer();

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
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).text( text ).build();
        renderer = new TextRenderer();

        // exercise
        portalResponse = renderer.render( textComponent, portalRequest );

        // verify
        assertEquals( "<section data-portal-component-type=\"text\">" + text + "</section>", portalResponse.getAsString() );
    }

    @Test
    public void textComponentWithSomeTextAndRenderModeEdit()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        String text = "<h2>hello</h2><p>How are you?</p>";
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).text( text ).build();
        renderer = new TextRenderer();

        // exercise
        portalResponse = renderer.render( textComponent, portalRequest );

        // verify
        assertEquals( "<div data-portal-component-type=\"text\"><section>" + text + "</section></div>", portalResponse.getAsString() );
    }
}