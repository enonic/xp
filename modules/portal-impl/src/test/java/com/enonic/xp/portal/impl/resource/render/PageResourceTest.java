package com.enonic.xp.portal.impl.resource.render;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.rendering.Renderable;
import com.enonic.xp.portal.rendering.RenderResult;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.RenderMode;

import static org.junit.Assert.*;

public class PageResourceTest
    extends RenderBaseResourceTest
{
    private Renderer<Renderable> renderer;

    @Override
    @SuppressWarnings("unchecked")
    protected void configure()
        throws Exception
    {
        final RendererFactory rendererFactory = Mockito.mock( RendererFactory.class );
        this.services.setRendererFactory( rendererFactory );

        this.renderer = Mockito.mock( Renderer.class );
        Mockito.when( rendererFactory.getRenderer( Mockito.any( Renderable.class ) ) ).thenReturn( this.renderer );

        super.configure();
    }

    @Test
    public void getContentFound()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        final RenderResult result = RenderResult.newRenderResult().
            entity( "content rendered" ).
            header( "some-heaer", "some-value" ).
            status( 200 ).
            build();
        Mockito.when( this.renderer.render( Mockito.any(), Mockito.any() ) ).thenReturn( result );

        MockHttpServletRequest request = newGetRequest( "/master/site/somepath/content" );
        MockHttpServletResponse response = executeRequest( request );

        ArgumentCaptor<PortalContext> jsContext = ArgumentCaptor.forClass( PortalContext.class );
        ArgumentCaptor<Renderable> renderable = ArgumentCaptor.forClass( Renderable.class );
        Mockito.verify( this.renderer ).render( renderable.capture(), jsContext.capture() );

        assertEquals( 200, response.getStatus() );
        assertEquals( "text/plain", response.getContentType() );
    }

    @Test
    public void getContentNotFound()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( Mockito.anyObject() ) ).thenReturn( null );

        final MockHttpServletRequest request = newGetRequest( "/master/site/somepath/content" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void getContentWithTemplateNotFound()
        throws Exception
    {
        setupContentAndSite();

        final MockHttpServletRequest request = newGetRequest( "/master/site/somepath/content" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void renderForNoPageDescriptor()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        final RenderResult result = RenderResult.newRenderResult().
            entity( "content rendered" ).
            header( "some-heaer", "some-value" ).
            status( 200 ).
            build();
        Mockito.when( this.renderer.render( Mockito.any(), Mockito.any() ) ).thenReturn( result );

        final PortalContext newContext = new PortalContext();
        newContext.setMode( RenderMode.EDIT );

        final MockHttpServletRequest request = newGetRequest( "/master/id" );
        PortalContextAccessor.set( request, newContext );

        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
    }
}
