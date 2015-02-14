package com.enonic.xp.portal.impl.rendering;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.page.region.TextComponent;
import com.enonic.xp.portal.impl.rendering.TextRenderer;
import com.enonic.xp.portal.rendering.RenderResult;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.RenderMode;

import static org.junit.Assert.*;

public class TextRendererTest
{
    private PortalContext context;

    private TextComponent textComponent;

    private TextRenderer renderer;

    @Before
    public void before()
    {
        context = new PortalContext();
        context.setMode( RenderMode.LIVE );
    }

    @Test
    public void textComponentWithNoText()
    {
        // setup
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).build();
        renderer = new TextRenderer();

        // exercise
        RenderResult result = renderer.render( textComponent, context );

        // verify
        assertEquals( "", result.getAsString() );
    }

    @Test
    public void textComponentWithNoTextAndRenderModeEdit()
    {
        // setup
        context.setMode( RenderMode.EDIT );
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).build();
        renderer = new TextRenderer();

        // exercise
        RenderResult result = renderer.render( textComponent, context );

        // verify
        assertEquals( "<div data-portal-component-type=\"text\"><section></section></div>", result.getAsString() );
    }

    @Test
    public void textComponentWithNoTextAndRenderModePreview()
    {
        // setup
        context.setMode( RenderMode.PREVIEW );
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).build();
        renderer = new TextRenderer();

        // exercise
        RenderResult result = renderer.render( textComponent, context );

        // verify
        assertEquals( "<section data-portal-component-type=\"text\"></section>", result.getAsString() );
    }


    @Test
    public void textComponentWithSomeTextAndRenderModePreview()
    {
        // setup
        String text = "<h2>hello</h2><p>How are you?</p>";
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).text( text ).build();
        renderer = new TextRenderer();

        // exercise
        RenderResult result = renderer.render( textComponent, context );

        // verify
        assertEquals( "<section data-portal-component-type=\"text\">" + text + "</section>", result.getAsString() );
    }

    @Test
    public void textComponentWithSomeTextAndRenderModeEdit()
    {
        // setup
        context.setMode( RenderMode.EDIT );
        String text = "<h2>hello</h2><p>How are you?</p>";
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).text( text ).build();
        renderer = new TextRenderer();

        // exercise
        RenderResult result = renderer.render( textComponent, context );

        // verify
        assertEquals( "<div data-portal-component-type=\"text\"><section>" + text + "</section></div>", result.getAsString() );
    }
}