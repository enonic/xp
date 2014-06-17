package com.enonic.wem.portal.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;

import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.rendering.RenderResult;
import com.enonic.wem.portal.rendering.Renderer;
import com.enonic.wem.portal.rendering.RendererFactory;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ComponentHandlerTest
    extends RenderBaseHandlerTest<ComponentHandler>
{
    private Renderer<Renderable> renderer;

    @Override
    protected ComponentHandler createResource()
    {
        return new ComponentHandler();
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setup()
        throws Exception
    {
        final RendererFactory rendererFactory = Mockito.mock( RendererFactory.class );
        this.resource.rendererFactory = rendererFactory;
        this.renderer = Mockito.mock( Renderer.class );

        when( rendererFactory.getRenderer( any( Renderable.class ) ) ).thenReturn( this.renderer );

        super.setup();
        mockCurrentContextHttpRequest();
    }

    @Test
    public void getComponentFound()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();
        final RenderResult result = RenderResult.newRenderResult().
            entity( "component rendered" ).
            header( "some-heaer", "some-value" ).
            status( 200 ).
            build();
        when( this.renderer.render( any(), any() ) ).thenReturn( result );

        final ClientResponse response = executeGet( "/live/site/somepath/content/_/component/main-region/0" );

        final ArgumentCaptor<JsContext> jsContext = ArgumentCaptor.forClass( JsContext.class );
        final ArgumentCaptor<Renderable> renderable = ArgumentCaptor.forClass( Renderable.class );
        verify( this.renderer ).render( renderable.capture(), jsContext.capture() );

        assertEquals( 200, response.getStatus() );
        assertEquals( "text/plain", response.getType().toString() );
        assertEquals( "component rendered", response.getEntity( String.class ) );
    }

    @Test(expected = SiteTemplateNotFoundException.class)
    public void getComponentPageNotFound()
        throws Exception
    {
        setupNonPageContent();

        executeGet( "/live/site/somepath/content/_/component/main-region/0" );
    }

    @Test
    public void getComponentNotFound()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        final ClientResponse response = executeGet( "/live/site/somepath/content/_/component/main-region/666" );
        assertEquals( 404, response.getStatus() );
    }
}
