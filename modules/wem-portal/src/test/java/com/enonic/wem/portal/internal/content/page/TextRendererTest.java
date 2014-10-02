package com.enonic.wem.portal.internal.content.page;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.page.text.TextComponent;
import com.enonic.wem.portal.RenderingMode;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.controller.JsHttpRequest;
import com.enonic.wem.portal.internal.controller.JsHttpResponse;
import com.enonic.wem.portal.internal.rendering.RenderResult;

import static org.junit.Assert.*;

public class TextRendererTest
{
    private JsContext context;

    private TextComponent textComponent;

    private TextRenderer renderer;

    private JsHttpRequest jsHttpRequest;

    private JsHttpResponse jsHttpResponse;

    @Before
    public void before()
    {
        jsHttpRequest = new JsHttpRequest();
        jsHttpRequest.setMode( RenderingMode.LIVE );

        jsHttpResponse = new JsHttpResponse();

        context = new JsContext();
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
        jsHttpRequest.setMode( RenderingMode.EDIT );
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
        jsHttpRequest.setMode( RenderingMode.PREVIEW );
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
        jsHttpRequest.setMode( RenderingMode.EDIT );
        String text = "<h2>hello</h2><p>How are you?</p>";
        textComponent = TextComponent.newTextComponent().name( "myTextComponent" ).text( text ).build();
        renderer = new TextRenderer();

        // exercise
        RenderResult result = renderer.render( textComponent, context );

        // verify
        assertEquals( "<div data-live-edit-type=\"text\">" + text + "</div>", result.getAsString() );
    }
}