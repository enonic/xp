package com.enonic.xp.portal.impl.handler.render;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.net.MediaType;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.Reference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;

import static org.junit.Assert.*;

public class PageHandlerTest
    extends RenderBaseHandlerTest
{
    private PageHandler handler;

    @Before
    public final void setup()
        throws Exception
    {
        super.setup();

        this.handler = new PageHandler();
        this.handler.setContentService( this.contentService );
        this.handler.setPageDescriptorService( this.pageDescriptorService );
        this.handler.setPageTemplateService( this.pageTemplateService );
        this.handler.setRendererFactory( this.rendererFactory );
        this.handler.setPortalUrlService( this.portalUrlService );

        this.request.setMethod( HttpMethod.GET );
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setEndpointPath( null );
    }

    @Test
    public void testOrder()
    {
        assertEquals( 50, this.handler.getOrder() );
    }

    @Test
    public void testMatch()
    {
        assertEquals( true, this.handler.canHandle( this.request ) );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        final PortalResponse portalResponse = PortalResponse.create().
            status( HttpStatus.METHOD_NOT_ALLOWED ).
            build();

        setRendererResult( portalResponse );

        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE", res.getHeaders().get( "Allow" ) );
    }


    @Test
    public void getContentFound()
        throws Exception
    {
        setupContentAndSite();
        setupTemplates();

        final PortalResponse portalResponse = PortalResponse.create().
            body( "component rendered" ).
            header( "some-header", "some-value" ).
            status( HttpStatus.OK ).
            build();

        setRendererResult( portalResponse );

        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PLAIN_TEXT_UTF_8, res.getContentType() );
        assertEquals( "some-value", res.getHeaders().get( "some-header" ) );
        assertEquals( "component rendered", res.getBody() );
    }


    @Test
    public void getContentNotFound()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( Mockito.anyObject() ) ).thenReturn( null );
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Page [/site/somepath/content] not found", e.getMessage() );
        }
    }

    @Test
    public void getContentWithTemplateNotFound()
        throws Exception
    {
        setupContentAndSite();
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Page template [my-page] not found", e.getMessage() );
        }
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
            status( HttpStatus.OK ).
            build();

        setRendererResult( portalResponse );

        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setMode( RenderMode.EDIT );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PLAIN_TEXT_UTF_8, res.getContentType() );
        assertEquals( "some-value", res.getHeaders().get( "some-header" ) );
        assertEquals( "content rendered", res.getBody() );
    }

    @Test
    public void getContentNotEnoughPermissions()
        throws Exception
    {
        Mockito.when( this.contentService.getByPath( Mockito.anyObject() ) ).thenReturn( null );
        Mockito.when( this.contentService.contentExists( Mockito.any( ContentPath.class ) ) ).thenReturn( true );

        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.UNAUTHORIZED, e.getStatus() );
            assertEquals( "You don't have permission to access [/site/somepath/content]", e.getMessage() );
        }
    }

    @Test
    public void getContentShortcut()
        throws Exception
    {
        final PropertyTree rootDataSet = new PropertyTree();
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

        this.request.setContentPath( ContentPath.from( "/site/somepath/shortcut" ) );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.TEMPORARY_REDIRECT, res.getStatus() );
        assertEquals( "/master/site/otherpath", res.getHeaders().get( "Location" ) );
    }

    @Test
    public void getContentShortcutWithParams()
        throws Exception
    {
        final PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addReference( "target", Reference.from( "ref" ) );

        final PropertySet shortcutParam1 = new PropertySet();
        shortcutParam1.addString( "name", "product" );
        shortcutParam1.addString( "value", "123456" );
        final PropertySet shortcutParam2 = new PropertySet();
        shortcutParam2.addString( "name", "order" );
        shortcutParam2.addString( "value", "abcdef" );
        rootDataSet.addSet( "parameters", shortcutParam1 );
        rootDataSet.addSet( "parameters", shortcutParam2 );

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
        Mockito.when( this.portalUrlService.pageUrl( Mockito.any( PageUrlParams.class ) ) ).thenReturn(
            "/master/site/otherpath?product=123456&order=abcdef" );

        this.request.setContentPath( ContentPath.from( "/site/somepath/shortcut" ) );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.TEMPORARY_REDIRECT, res.getStatus() );
        assertEquals( "/master/site/otherpath?product=123456&order=abcdef", res.getHeaders().get( "Location" ) );
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
            status( HttpStatus.OK ).
            build();

        setRendererResult( portalResponse );

        this.request.setContentPath( ContentPath.from( "/id" ) );
        this.request.setMode( RenderMode.EDIT );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PLAIN_TEXT_UTF_8, res.getContentType() );
        assertEquals( "some-value", res.getHeaders().get( "some-header" ) );
        assertEquals( "content rendered", res.getBody() );
    }
}
