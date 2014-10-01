package com.enonic.wem.portal.internal.content;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.workspace.Workspaces;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.wem.portal.internal.rendering.RendererFactory;

import static org.junit.Assert.*;

public class ComponentResourceTest
    extends RenderBaseResourceTest<ComponentResourceProvider>
{
    private Renderer<Renderable> renderer;

    private final Workspace testWorkspace = Workspace.from( "test" );

    private final Context testContext = Context.create().
        workspace( testWorkspace ).
        repository( Repository.create().
            id( RepositoryId.from( "testing" ) ).
            workspaces( Workspaces.from( testWorkspace ) ).
            build() ).
        build();

    @Override
    @SuppressWarnings("unchecked")
    protected void configure()
        throws Exception
    {
        this.resourceProvider = new ComponentResourceProvider();

        final RendererFactory rendererFactory = Mockito.mock( RendererFactory.class );
        this.resourceProvider.setRendererFactory( rendererFactory );

        this.renderer = Mockito.mock( Renderer.class );
        Mockito.when( rendererFactory.getRenderer( Mockito.any( Renderable.class ) ) ).thenReturn( this.renderer );

        super.configure();
    }

    @Test
    public void getComponentFound()
        throws Exception
    {
        setupContentAndSite( testContext );
        setupTemplates();
        final RenderResult result = RenderResult.newRenderResult().
            entity( "component rendered" ).
            header( "some-heaer", "some-value" ).
            status( 200 ).
            build();
        Mockito.when( this.renderer.render( Mockito.any(), Mockito.any() ) ).thenReturn( result );

        final MockHttpServletRequest request = newGetRequest( "/live/test/site/somepath/content/_/component/main-region/0" );
        final MockHttpServletResponse response = executeRequest( request );

        final ArgumentCaptor<JsContext> jsContext = ArgumentCaptor.forClass( JsContext.class );
        final ArgumentCaptor<Renderable> renderable = ArgumentCaptor.forClass( Renderable.class );
        Mockito.verify( this.renderer ).render( renderable.capture(), jsContext.capture() );

        assertEquals( 200, response.getStatus() );
        assertEquals( "text/plain", response.getContentType() );
        assertEquals( "component rendered", response.getContentAsString() );
    }

    @Test
    public void getComponentPageNotFound()
        throws Exception
    {
        setupNonPageContent( testContext );

        final MockHttpServletRequest request = newGetRequest( "/live/test/site/somepath/content/_/component/main-region/0" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void getComponentNotFound()
        throws Exception
    {
        setupContentAndSite( testContext );
        setupTemplates();

        final MockHttpServletRequest request = newGetRequest( "/live/test/site/somepath/content/_/component/main-region/666" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
    }
}
