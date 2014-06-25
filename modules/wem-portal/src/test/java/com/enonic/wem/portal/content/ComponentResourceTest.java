package com.enonic.wem.portal.content;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.rendering.RenderResult;
import com.enonic.wem.portal.rendering.Renderer;
import com.enonic.wem.portal.rendering.RendererFactory;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ComponentResourceTest
    extends RenderBaseResourceTest<ComponentResource>
{
    private Renderer<Renderable> renderer;

    @Override
    @SuppressWarnings("unchecked")
    protected void configure()
        throws Exception
    {
        this.resource = new ComponentResource();
        final RendererFactory rendererFactory = Mockito.mock( RendererFactory.class );
        this.resource.rendererFactory = rendererFactory;
        this.renderer = Mockito.mock( Renderer.class );

        when( rendererFactory.getRenderer( any( Renderable.class ) ) ).thenReturn( this.renderer );

        super.configure();
        mockCurrentContextHttpRequest();
    }

    @Test
    public void getComponentFound()
        throws Exception
    {
        setupContentAndSite( Context.create( Workspace.from( "test" ) ) );
        setupTemplates();
        final RenderResult result = RenderResult.newRenderResult().
            entity( "component rendered" ).
            header( "some-heaer", "some-value" ).
            status( 200 ).
            build();
        when( this.renderer.render( any(), any() ) ).thenReturn( result );

        final Request request = new Request( Method.GET, "/live/test/site/somepath/content/_/component/main-region/0" );
        final Response response = executeRequest( request );

        final ArgumentCaptor<JsContext> jsContext = ArgumentCaptor.forClass( JsContext.class );
        final ArgumentCaptor<Renderable> renderable = ArgumentCaptor.forClass( Renderable.class );
        verify( this.renderer ).render( renderable.capture(), jsContext.capture() );

        assertEquals( 200, response.getStatus().getCode() );
        assertEquals( "text/plain", response.getEntity().getMediaType().toString() );
        assertEquals( "site/somepath/content", this.resource.contentPath );
        assertEquals( "component rendered", response.getEntityAsText() );
    }

    @Test
    public void getComponentPageNotFound()
        throws Exception
    {
        setupNonPageContent( Context.create( Workspace.from( "test" ) ) );

        final Request request = new Request( Method.GET, "/live/test/site/somepath/content/_/component/main-region/0" );
        final Response response = executeRequest( request );

        assertEquals( 404, response.getStatus().getCode() );
    }

    @Test
    public void getComponentNotFound()
        throws Exception
    {
        setupContentAndSite( Context.create( Workspace.from( "test" ) ) );
        setupTemplates();

        final Request request = new Request( Method.GET, "/live/test/site/somepath/content/_/component/main-region/666" );
        final Response response = executeRequest( request );

        assertEquals( 404, response.getStatus().getCode() );
    }
}
