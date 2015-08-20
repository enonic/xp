package com.enonic.xp.portal.impl.resource.render;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.region.Component;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class ComponentResourceTest
    extends RenderBaseResourceTest
{
    private Renderer renderer;

    @Override
    @SuppressWarnings("unchecked")
    protected void configure()
        throws Exception
    {
        final RendererFactory rendererFactory = Mockito.mock( RendererFactory.class );
        this.services.setRendererFactory( rendererFactory );

        this.renderer = Mockito.mock( Renderer.class );
        Mockito.when( rendererFactory.getRenderer( Mockito.any( Object.class ) ) ).thenReturn( this.renderer );

        super.configure();
    }

    @Test
    public void getComponentFound()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();
        final PortalResponse portalResponse = PortalResponse.create().
            body( "component rendered" ).
            header( "some-heaer", "some-value" ).
            status( 200 ).
            build();
        Mockito.when( this.renderer.render( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        final MockHttpServletRequest request = newGetRequest( "/master/site/somepath/content/_/component/main-region/0" );
        ServletRequestHolder.setRequest( request );
        final MockHttpServletResponse response = executeRequest( request );

        final ArgumentCaptor<PortalRequest> jsRequest = ArgumentCaptor.forClass( PortalRequest.class );
        final ArgumentCaptor<Object> renderable = ArgumentCaptor.forClass( Object.class );
        Mockito.verify( this.renderer ).render( renderable.capture(), jsRequest.capture() );

        assertEquals( 200, response.getStatus() );
        assertEquals( "text/plain; charset=utf-8", response.getContentType() );
        assertEquals( "component rendered", response.getContentAsString() );
    }

    @Test
    public void getComponentPageNotFound()
        throws Exception
    {
        setupNonPageContent();

        final MockHttpServletRequest request = newGetRequest( "/master/site/somepath/content/_/component/main-region/0" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void getComponentNotFound()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        final MockHttpServletRequest request = newGetRequest( "/master/site/somepath/content/_/component/main-region/666" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void verifyUrlSet()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();
        final PortalResponse portalResponse = PortalResponse.create().
            body( "component rendered" ).
            header( "some-heaer", "some-value" ).
            status( 200 ).
            build();
        Mockito.when( this.renderer.render( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        final MockHttpServletRequest request = newGetRequest( "/master/site/somepath/content/_/component/main-region/0" );
        ServletRequestHolder.setRequest( request );
        final MockHttpServletResponse response = executeRequest( request );

        final ArgumentCaptor<PortalRequest> jsRequest = ArgumentCaptor.forClass( PortalRequest.class );
        final ArgumentCaptor<Component> renderable = ArgumentCaptor.forClass( Component.class );
        Mockito.verify( this.renderer ).render( renderable.capture(), jsRequest.capture() );

        assertEquals( "http", jsRequest.getValue().getScheme() );
        assertEquals( "localhost", jsRequest.getValue().getHost() );
        assertEquals( "80", jsRequest.getValue().getPort() );
        assertEquals( "/portal/master/site/somepath/content/_/component/main-region/0", jsRequest.getValue().getPath() );
        assertEquals( "http://localhost/portal/master/site/somepath/content/_/component/main-region/0", jsRequest.getValue().getUrl() );
    }
}
