package com.enonic.xp.portal.impl.resource.render;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.CounterPropertyIdProvider;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.Page;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.Reference;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class PageResourceTest
    extends RenderBaseResourceTest
{
    private Renderer<Page> renderer;

    @Override
    @SuppressWarnings("unchecked")
    protected void configure()
        throws Exception
    {
        final RendererFactory rendererFactory = Mockito.mock( RendererFactory.class );
        this.services.setRendererFactory( rendererFactory );

        this.renderer = Mockito.mock( Renderer.class );
        Mockito.when( rendererFactory.getRenderer( Mockito.any( Page.class ) ) ).thenReturn( this.renderer );

        super.configure();
    }

    @Test
    public void getContentFound()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        final PortalResponse portalResponse = PortalResponse.create().
            body( "content rendered" ).
            header( "some-header", "some-value" ).
            status( 200 ).
            build();
        Mockito.when( this.renderer.render( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        MockHttpServletRequest request = newGetRequest( "/master/site/somepath/content" );
        MockHttpServletResponse response = executeRequest( request );

        ArgumentCaptor<PortalRequest> jsRequest = ArgumentCaptor.forClass( PortalRequest.class );
        ArgumentCaptor<Page> renderable = ArgumentCaptor.forClass( Page.class );
        Mockito.verify( this.renderer ).render( renderable.capture(), jsRequest.capture() );

        assertEquals( 200, response.getStatus() );
        assertEquals( "text/plain; charset=utf-8", response.getContentType() );
    }

    @Test
    public void verifyUrlSet()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        final PortalResponse portalResponse = PortalResponse.create().
            body( "content rendered" ).
            header( "some-header", "some-value" ).
            status( 200 ).
            build();
        Mockito.when( this.renderer.render( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        MockHttpServletRequest request = newGetRequest( "/master/site/somepath/content" );
        ServletRequestHolder.setRequest( request );
        MockHttpServletResponse response = executeRequest( request );

        ArgumentCaptor<PortalRequest> jsRequest = ArgumentCaptor.forClass( PortalRequest.class );
        ArgumentCaptor<Page> renderable = ArgumentCaptor.forClass( Page.class );
        Mockito.verify( this.renderer ).render( renderable.capture(), jsRequest.capture() );

        assertEquals( "http", jsRequest.getValue().getScheme() );
        assertEquals( "localhost", jsRequest.getValue().getHost() );
        assertEquals( "80", jsRequest.getValue().getPort() );
        assertEquals( "/portal/master/site/somepath/content", jsRequest.getValue().getPath() );
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

        final PortalResponse portalResponse = PortalResponse.create().
            body( "content rendered" ).
            header( "some-header", "some-value" ).
            status( 200 ).
            build();
        Mockito.when( this.renderer.render( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        final PortalRequest newPortalRequest = new PortalRequest();
        newPortalRequest.setMode( RenderMode.EDIT );

        final MockHttpServletRequest request = newGetRequest( "/master/id" );
        PortalRequestAccessor.set( request, newPortalRequest );

        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
    }

    @Test
    public void getContentNotEnoughPermissions()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( Mockito.anyObject() ) ).thenReturn( null );
        Mockito.when( this.contentService.contentExists( Mockito.any( ContentPath.class ) ) ).thenReturn( true );

        final MockHttpServletRequest request = newGetRequest( "/master/site/somepath/content" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 403, response.getStatus() );
    }

    @Test
    public void getContentShortcut()
        throws Exception
    {
        final PropertyTree rootDataSet = new PropertyTree( new CounterPropertyIdProvider() );
        rootDataSet.addReference( "target", Reference.from( "ref" ) );

        final Content content = Content.create().
            id( ContentId.from( "id" ) ).
            path( ContentPath.from( "site/somepath/shortcut" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.shortcut() ).
            data( rootDataSet ).
            build();

        Mockito.when( this.contentService.getByPath( content.getPath().asAbsolute() ) ).thenReturn( content );

        Mockito.when( this.portalUrlService.pageUrl( Mockito.any( PageUrlParams.class ) ) ).thenReturn( "/master/site/otherpath" );

        MockHttpServletRequest request = newGetRequest( "/master/site/somepath/shortcut" );
        MockHttpServletResponse response = executeRequest( request );

        assertEquals( 307, response.getStatus() );
        assertEquals( "/master/site/otherpath", response.getHeader( "location" ) );
    }

    @Test
    public void renderCustomizedTemplate()
        throws Exception
    {
        setupCustomizedTemplateContentAndSite();
        setupController();

        final PortalResponse portalResponse = PortalResponse.create().
            body( "content rendered" ).
            header( "some-header", "some-value" ).
            status( 200 ).
            build();
        Mockito.when( this.renderer.render( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        final PortalRequest newPortalRequest = new PortalRequest();
        newPortalRequest.setMode( RenderMode.EDIT );

        final MockHttpServletRequest request = newGetRequest( "/master/id" );
        PortalRequestAccessor.set( request, newPortalRequest );

        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
    }
}
