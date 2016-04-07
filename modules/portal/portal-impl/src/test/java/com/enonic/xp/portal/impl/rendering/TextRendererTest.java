package com.enonic.xp.portal.impl.rendering;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.url.PortalUrlServiceImpl;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.region.TextComponent;

import static org.junit.Assert.*;

public class TextRendererTest
{
    private PortalRequest portalRequest;

    private PortalResponse portalResponse;

    private TextComponent textComponent;

    private TextRenderer renderer;

    private PortalUrlService service;

    @Before
    public void before()
    {
        portalRequest = new PortalRequest();
        portalResponse = PortalResponse.create().build();
        service = new PortalUrlServiceImpl();
        portalRequest.setMode( RenderMode.LIVE );
    }

    @Test
    public void textComponentWithNoText()
    {
        // setup
        textComponent = TextComponent.create().name( "myTextComponent" ).build();
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
        textComponent = TextComponent.create().name( "myTextComponent" ).build();
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
        textComponent = TextComponent.create().name( "myTextComponent" ).build();
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
        textComponent = TextComponent.create().name( "myTextComponent" ).text( text ).build();
        renderer = new TextRenderer();
        renderer.setPortalUrlService( service );

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
        textComponent = TextComponent.create().name( "myTextComponent" ).text( text ).build();
        renderer = new TextRenderer();
        renderer.setPortalUrlService( service );

        // exercise
        portalResponse = renderer.render( textComponent, portalRequest );

        // verify
        assertEquals( "<div data-portal-component-type=\"text\"><section>" + text + "</section></div>", portalResponse.getAsString() );
    }
}