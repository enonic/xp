package com.enonic.wem.portal.internal.rendering.page.region;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.page.region.TextComponent;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.controller.PortalRequestImpl;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;

import static org.junit.Assert.*;

public class TextRendererTest
{
    private PortalContextImpl context;

    private TextComponent textComponent;

    private TextRenderer renderer;

    private PortalRequestImpl jsHttpRequest;

    private PortalResponse jsHttpResponse;

    @Before
    public void before()
    {
        jsHttpRequest = new PortalRequestImpl();
        jsHttpRequest.setMode( RenderMode.LIVE );

        jsHttpResponse = new PortalResponse();

        context = new PortalContextImpl();
        context.setRequest( jsHttpRequest );
        context.setResponse( jsHttpResponse );
    }

    @Test
    public void given_TextComponent_with_no_text_when_render_then_emtpy_string_text_is_returned()
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
    public void given_TextComponent_with_no_text_and_RenderMode_is_EDIT_when_render_then_empty_LiveEdit_div_is_returned()
    {
        // setup
        jsHttpRequest.setMode( RenderMode.EDIT );
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).build();
        renderer = new TextRenderer();

        // exercise
        RenderResult result = renderer.render( textComponent, context );

        // verify
        assertEquals(
            "<div data-live-edit-type=\"text\" data-live-edit-empty-component=\"true\" class=\"live-edit-empty-component\"></div>",
            result.getAsString() );
    }

    @Test
    public void given_TextComponent_with_no_text_and_RenderMode_is_PREVIEW_when_render_then_empty_div_is_returned()
    {
        // setup
        jsHttpRequest.setMode( RenderMode.PREVIEW );
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).build();
        renderer = new TextRenderer();

        // exercise
        RenderResult result = renderer.render( textComponent, context );

        // verify
        assertEquals( "<div></div>", result.getAsString() );
    }


    @Test
    public void given_TextComponent_with_some_text_when_render_then_text_is_returned()
    {
        // setup
        String text = "<h2>hello</h2><p>How are you?</p>";
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).text( text ).build();
        renderer = new TextRenderer();

        // exercise
        RenderResult result = renderer.render( textComponent, context );

        // verify
        assertEquals( text, result.getAsString() );
    }

    @Test
    public void given_TextComponent_with_some_text_and_RenderMode_is_EDIT_when_render_then_result_is_wrapped_by_LiveEdit_div()
    {
        // setup
        jsHttpRequest.setMode( RenderMode.EDIT );
        String text = "<h2>hello</h2><p>How are you?</p>";
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).text( text ).build();
        renderer = new TextRenderer();

        // exercise
        RenderResult result = renderer.render( textComponent, context );

        // verify
        assertEquals( "<div data-live-edit-type=\"text\">" + text + "</div>", result.getAsString() );
    }
}