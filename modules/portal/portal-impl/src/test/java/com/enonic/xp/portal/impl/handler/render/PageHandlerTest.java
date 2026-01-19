package com.enonic.xp.portal.impl.handler.render;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.Reference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PageHandlerTest
    extends RenderBaseHandlerTest
{
    private PageHandler handler;

    @BeforeEach
    public final void setup()
    {
        this.handler = new PageHandler();
        this.handler.setPageDescriptorService( this.pageDescriptorService );
        this.handler.setLayoutDescriptorService( this.layoutDescriptorService );
        this.handler.setPageTemplateService( this.pageTemplateService );
        this.handler.setRendererDelegate( this.rendererDelegate );
        this.handler.setPortalUrlService( this.portalUrlService );

        this.request.setMethod( HttpMethod.GET );
        this.request.setBaseUri( "/site" );
        this.request.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setEndpointPath( null );
    }

    @Test
    void testOrder()
    {
        assertEquals( 50, this.handler.getOrder() );
    }

    @Test
    void testMatch()
    {
        assertTrue( this.handler.canHandle( this.request ) );
    }

    @Test
    void testOptions()
        throws Exception
    {
        setupSite();
        setupContent();
        setupTemplates();

        final PortalResponse portalResponse = PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();

        setRendererResult( portalResponse );

        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE,PATCH", res.getHeaders().get( "Allow" ) );
    }


    @Test
    void getContentFound()
        throws Exception
    {
        setupSite();
        setupContent();
        setupTemplates();

        final PortalResponse portalResponse =
            PortalResponse.create().body( "component rendered" ).header( "some-header", "some-value" ).status( HttpStatus.OK ).build();

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
    void getContentNotFound()
    {
        final ContentPath path = ContentPath.from( "/site/somepath/content" );
        when( this.contentService.getByPath( path ) ).thenThrow( ContentNotFoundException.class );
        this.request.setContentPath( path );

        final WebException e =
            assertThrows( WebException.class, () -> this.handler.handle( this.request, PortalResponse.create().build(), null ) );
        assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
        assertEquals( "Page [/site/somepath/content] not found", e.getMessage() );
    }

    @Test
    void getContentExistsButNeedsAuthentication()
    {
        final ContentPath path = ContentPath.from( "/site/somepath/content" );

        final Content content = Content.create()
            .id( ContentId.from( "id" ) )
            .path( path )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .displayName( "My Content" )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .type( ContentTypeName.shortcut() )
            .data( new PropertyTree() )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.ADMIN ).build() )
                              .build() )
            .build();

        this.request.setContent( content );
        this.request.setContentPath( path );

        final WebException e =
            assertThrows( WebException.class, () -> this.handler.handle( this.request, PortalResponse.create().build(), null ) );
        assertEquals( HttpStatus.UNAUTHORIZED, e.getStatus() );
        assertEquals( "You don't have permission to access [/site/somepath/content]", e.getMessage() );
    }

    @Test
    void getContentExistsButInsufficientRights()
    {
        final AuthenticationInfo authenticationInfo = AuthenticationInfo.create().user( User.anonymous() ).build();
        final Context authenticatedContext = ContextBuilder.from( ContextAccessor.current() ).authInfo( authenticationInfo ).build();

        final ContentPath path = ContentPath.from( "/site/somepath/content" );

        final Content content = Content.create()
            .id( ContentId.from( "id" ) )
            .path( path )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .displayName( "My Content" )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .type( ContentTypeName.shortcut() )
            .data( new PropertyTree() )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.ADMIN ).build() )
                              .build() )
            .build();

        this.request.setContent( content );
        this.request.setContentPath( path );

        final WebException e = assertThrows( WebException.class, () -> authenticatedContext.callWith(
            () -> this.handler.handle( this.request, PortalResponse.create().build(), null ) ) );
        assertEquals( HttpStatus.FORBIDDEN, e.getStatus() );
        assertEquals( "You don't have permission to access [/site/somepath/content]", e.getMessage() );
    }

    @Test
    void getSiteNotFound()
    {
        setupContent();

        final ContentPath path = ContentPath.from( "/site/somepath/content" );
        when( this.contentService.findNearestSiteByPath( path ) ).thenReturn( null );

        this.request.setContentPath( path );

        final WebException e =
            assertThrows( WebException.class, () -> this.handler.handle( this.request, PortalResponse.create().build(), null ) );
        assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
        assertEquals( "Site for [/site/somepath/content] not found", e.getMessage() );
    }

    @Test
    void getContentWithTemplateNotFound()
    {
        setupSite();
        setupContent();

        when( this.pageTemplateService.getByKey( eq( PageTemplateKey.from( "my-page" ) ) ) ).thenThrow( ContentNotFoundException.class );

        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );

        final WebException e =
            assertThrows( WebException.class, () -> this.handler.handle( this.request, PortalResponse.create().build(), null ) );
        assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
        assertEquals( "Template [my-page] is missing and no default template found for content", e.getMessage() );
    }

    @Test
    void renderForNoPageDescriptor()
        throws Exception
    {
        setupSite();
        setupContent();
        setupTemplates();

        final PortalResponse portalResponse =
            PortalResponse.create().body( "content rendered" ).header( "some-header", "some-value" ).status( HttpStatus.OK ).build();

        setRendererResult( portalResponse );

        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PLAIN_TEXT_UTF_8, res.getContentType() );
        assertEquals( "some-value", res.getHeaders().get( "some-header" ) );
        assertEquals( "content rendered", res.getBody() );
    }

    @Test
    void getContentShortcut()
        throws Exception
    {
        final PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addReference( "target", Reference.from( "ref" ) );

        final Content content = Content.create()
            .id( ContentId.from( "id" ) )
            .path( ContentPath.from( "site/somepath/shortcut" ) )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .displayName( "My Content" )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .type( ContentTypeName.shortcut() )
            .data( rootDataSet )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                              .build() )
            .build();

        when( this.portalUrlService.pageUrl( any( PageUrlParams.class ) ) ).thenReturn( "/master/site/otherpath" );

        this.request.setContent( content );
        this.request.setContentPath( content.getPath() );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.TEMPORARY_REDIRECT, res.getStatus() );
        assertEquals( "/master/site/otherpath", res.getHeaders().get( "Location" ) );
    }

    @Test
    void getContentShortcutWithParams()
        throws Exception
    {
        final PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addReference( "target", Reference.from( "ref" ) );

        final PropertySet shortcutParam1 = rootDataSet.newSet();
        shortcutParam1.addString( "name", "product" );
        shortcutParam1.addString( "value", "123456" );
        final PropertySet shortcutParam2 = rootDataSet.newSet();
        shortcutParam2.addString( "name", "order" );
        shortcutParam2.addString( "value", "abcdef" );
        rootDataSet.addSet( "parameters", shortcutParam1 );
        rootDataSet.addSet( "parameters", shortcutParam2 );

        final Content content = Content.create()
            .id( ContentId.from( "id" ) )
            .path( ContentPath.from( "site/somepath/shortcut" ) )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .displayName( "My Content" )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .type( ContentTypeName.shortcut() )
            .data( rootDataSet )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                              .build() )
            .build();

        this.request.setContent( content );

        when( this.portalUrlService.pageUrl( any( PageUrlParams.class ) ) ).thenReturn(
            "/master/site/otherpath?product=123456&order=abcdef" );

        this.request.setContentPath( content.getPath() );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.TEMPORARY_REDIRECT, res.getStatus() );
        assertEquals( "/master/site/otherpath?product=123456&order=abcdef", res.getHeaders().get( "Location" ) );
    }

    @Test
    void renderCustomizedTemplate()
        throws Exception
    {
        setupCustomizedTemplateContentAndSite();
        setupController();

        final PortalResponse portalResponse =
            PortalResponse.create().body( "content rendered" ).header( "some-header", "some-value" ).status( HttpStatus.OK ).build();

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
