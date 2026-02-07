package com.enonic.xp.portal.impl.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.net.MediaType;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.handler.render.RenderBaseHandlerTest;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.Regions;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComponentHandlerTest
    extends RenderBaseHandlerTest
{
    private ComponentHandler handler;

    @BeforeEach
    public final void setup()
    {
        this.handler = new ComponentHandler( this.contentService, this.rendererDelegate, this.pageTemplateService, this.postProcessor,
                                             this.pageDescriptorService, this.layoutDescriptorService );

        this.request.setBaseUri( "/site" );
        this.request.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        this.request.setMethod( HttpMethod.GET );
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setRawPath( "/_/component/main" );
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

        Mockito.when( this.postProcessor.processResponseInstructions( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        this.request.setMethod( HttpMethod.OPTIONS );
        this.request.setRawPath( "/_/component/main-region/0" );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE,PATCH", res.getHeaders().get( "Allow" ) );
    }

    @Test
    void testComponentFound()
        throws Exception
    {
        setupSite();
        setupContent();
        setupTemplates();

        final PortalResponse portalResponse =
            PortalResponse.create().body( "component rendered" ).header( "some-header", "some-value" ).status( HttpStatus.OK ).build();
        final ArgumentCaptor<PortalResponse> portalResponseCaptor = ArgumentCaptor.forClass( PortalResponse.class );
        Mockito.when( this.postProcessor.processResponseInstructions( Mockito.any(), portalResponseCaptor.capture() ) )
            .thenReturn( portalResponse );

        setRendererResult( portalResponse );

        this.request.setRawPath( "/_/component/main-region/0" );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PLAIN_TEXT_UTF_8, res.getContentType() );
        assertEquals( "some-value", res.getHeaders().get( "some-header" ) );
        assertEquals( "component rendered", res.getBody() );
        assertNull( portalResponseCaptor.getValue().getHeaders().get( "Has-Contributions" ) );
    }

    @Test
    void getComponentPageNotFound()
    {
        setupContentWithoutPage();

        Mockito.when( this.pageTemplateService.getByKey( Mockito.eq( PageTemplateKey.from( "my-page" ) ) ) )
            .thenThrow( ContentNotFoundException.class );

        this.request.setRawPath( "/_/component/main-region/0" );

        final WebException e = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertAll( () -> assertEquals( HttpStatus.NOT_FOUND, e.getStatus() ),
                   () -> assertEquals( "No default template found for content", e.getMessage() ) );
    }

    @Test
    void getComponentNotFound()
    {
        setupSite();
        setupContent();
        setupTemplates();

        this.request.setRawPath( "/_/component/main-region/666" );

        final WebException e = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
        assertEquals( "Page component for [/main-region/666] not found", e.getMessage() );
    }

    @Test
    void getContentNotFound()
    {
        this.request.setRawPath( "/_/component/main-region/666" );

        final ContentPath path = ContentPath.from( "/site/somepath/content" );
        Mockito.when( this.contentService.getByPath( path ) ).thenThrow( ContentNotFoundException.class );
        this.request.setContentPath( path );

        final WebException e = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
        assertEquals( "Page [/site/somepath/content] not found", e.getMessage() );
    }

    @Test
    void getSiteNotFound()
    {
        setupContent();
        this.request.setRawPath( "/_/component/main-region/666" );

        final ContentPath path = ContentPath.from( "/site/somepath/content" );
        Mockito.when( this.contentService.findNearestSiteByPath( path ) ).thenReturn( null );

        this.request.setContentPath( path );

        final WebException e = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
        assertEquals( "Site for [/site/somepath/content] not found", e.getMessage() );
    }

    @Test
    void testHasContributionsHeaderAdded()
        throws Exception
    {
        setupSite();
        setupContent();
        setupTemplates();

        final PortalResponse portalResponse =
            PortalResponse.create().contribution( HtmlTag.HEAD_END, "contribute" ).status( HttpStatus.OK ).build();

        final ArgumentCaptor<PortalResponse> portalResponseCaptor = ArgumentCaptor.forClass( PortalResponse.class );
        Mockito.when( this.postProcessor.processResponseInstructions( Mockito.any(), portalResponseCaptor.capture() ) )
            .thenReturn( portalResponse );

        setRendererResult( portalResponse );

        this.request.setMode( RenderMode.EDIT );
        this.request.setRawPath( "/_/component/main-region/0" );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "true", portalResponseCaptor.getValue().getHeaders().get( "X-Has-Contributions" ) );
    }

    @Test
    void testHasContributionsHeaderNotAddedInNonEditMode()
        throws Exception
    {
        setupSite();
        setupContent();
        setupTemplates();

        final PortalResponse portalResponse =
            PortalResponse.create().contribution( HtmlTag.HEAD_END, "contribute" ).status( HttpStatus.OK ).build();

        final ArgumentCaptor<PortalResponse> portalResponseCaptor = ArgumentCaptor.forClass( PortalResponse.class );
        Mockito.when( this.postProcessor.processResponseInstructions( Mockito.any(), portalResponseCaptor.capture() ) )
            .thenReturn( portalResponse );

        setRendererResult( portalResponse );

        this.request.setRawPath( "/_/component/main-region/0" );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertNull( portalResponseCaptor.getValue().getHeaders().get( "X-Has-Contributions" ) );
    }

    @Test
    void testHasContributionsHeaderNotAddedInEditModeWithoutContributions()
        throws Exception
    {
        setupSite();
        setupContent();
        setupTemplates();

        final PortalResponse portalResponse = PortalResponse.create().status( HttpStatus.OK ).build();

        final ArgumentCaptor<PortalResponse> portalResponseCaptor = ArgumentCaptor.forClass( PortalResponse.class );
        Mockito.when( this.postProcessor.processResponseInstructions( Mockito.any(), portalResponseCaptor.capture() ) )
            .thenReturn( portalResponse );

        setRendererResult( portalResponse );
        this.request.setMode( RenderMode.EDIT );
        this.request.setRawPath( "/_/component/main-region/0" );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertNull( portalResponseCaptor.getValue().getHeaders().get( "X-Has-Contributions" ) );
    }

    @Test
    void testEmptyFragmentIsResolved()
        throws Exception
    {
        setupSite();
        setupContent( createPageWithFragment( null ) );
        setupTemplates();

        final PortalResponse portalResponse =
            PortalResponse.create().body( "component rendered" ).header( "some-header", "some-value" ).status( HttpStatus.OK ).build();

        Mockito.when( this.postProcessor.processResponseInstructions( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        setRendererResult( portalResponse );

        this.request.setRawPath( "/_/component/main-region/0" );

        final WebResponse res = this.handler.handle( this.request );
        assertResponseOK( res );
    }

    @Test
    void testFragmentContentNotFound()
        throws Exception
    {
        setupSite();
        setupContent( createPageWithFragment() );
        setupTemplates();

        Mockito.when( this.contentService.getById( ContentId.from( "fragmentId" ) ) ).thenThrow( ContentNotFoundException.class );

        final PortalResponse portalResponse =
            PortalResponse.create().body( "component rendered" ).header( "some-header", "some-value" ).status( HttpStatus.OK ).build();

        Mockito.when( this.postProcessor.processResponseInstructions( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        setRendererResult( portalResponse );

        this.request.setRawPath( "/_/component/main-region/0" );

        final WebResponse res = this.handler.handle( this.request );
        assertResponseOK( res );
    }

    @Test
    void testFragmentWithoutComponentsIsResolved()
        throws Exception
    {
        setupSite();
        setupContent( createPageWithFragment() );
        setupTemplates();

        Mockito.when( this.contentService.getById( ContentId.from( "fragmentId" ) ) )
            .thenReturn( createContentWithPage( null, ContentTypeName.from( "contentTypeName" ) ) );

        final PortalResponse portalResponse =
            PortalResponse.create().body( "component rendered" ).header( "some-header", "some-value" ).status( HttpStatus.OK ).build();

        Mockito.when( this.postProcessor.processResponseInstructions( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        setRendererResult( portalResponse );

        this.request.setRawPath( "/_/component/main-region/0" );

        final WebResponse res = this.handler.handle( this.request );
        assertResponseOK( res );
    }

    @Test
    void testFragmentWithComponentsIsResolved()
        throws Exception
    {
        setupSite();
        setupContent( createPageWithFragment() );
        setupTemplates();

        Mockito.when( this.contentService.getById( ContentId.from( "fragmentId" ) ) )
            .thenReturn( createPageThatIsFragment( TextComponent.create().build() ) );

        final PortalResponse portalResponse =
            PortalResponse.create().body( "component rendered" ).header( "some-header", "some-value" ).status( HttpStatus.OK ).build();

        Mockito.when( this.postProcessor.processResponseInstructions( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        setRendererResult( portalResponse );

        this.request.setRawPath( "/_/component/main-region/0" );

        final WebResponse res = this.handler.handle( this.request );
        assertResponseOK( res );
    }

    @Test
    void testFragmentComponentAsTextComponent()
        throws Exception
    {
        final TextComponent textComponent = TextComponent.create().build();
        final Content content = createPageThatIsFragment( textComponent );
        setupContent( content );

        testFragmentContent( content, "/_/component/" );
    }

    @Test
    void testFragmentComponentAsLayoutComponent()
        throws Exception
    {
        final Regions regions =
            Regions.create().add( Region.create().name( "main-region" ).add( TextComponent.create().build() ).build() ).build();
        final LayoutComponent layoutComponent = LayoutComponent.create().regions( regions ).build();
        final Content content = createPageThatIsFragment( layoutComponent );

        testFragmentContent( content, "/_/component/main-region/0" );
    }

    @Test
    void testHandleMethodNotAllowed()
    {
        this.request.setMethod( HttpMethod.CONNECT );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method CONNECT not allowed", ex.getMessage() );
    }

    @Test
    void testHandleNotSiteBase()
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setBaseUri( "/unknown" );
        portalRequest.setRawPath( "/site/project/branch/content/_/component/pathToComponent" );
        portalRequest.setRawPath( "/_/component/pathToComponent" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( portalRequest ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Not a valid request", ex.getMessage() );
    }

    private void testFragmentContent( final Content content, final String endpointPath )
        throws Exception
    {
        setupSite();
        setupContent( content );

        final PortalResponse portalResponse =
            PortalResponse.create().body( "component rendered" ).header( "some-header", "some-value" ).status( HttpStatus.OK ).build();

        Mockito.when( this.postProcessor.processResponseInstructions( Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        setRendererResult( portalResponse );

        this.request.setRawPath( endpointPath );

        final WebResponse res = this.handler.handle( this.request );
        assertResponseOK( res );
    }

    private void assertResponseOK( final WebResponse res )
    {
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PLAIN_TEXT_UTF_8, res.getContentType() );
        assertEquals( "some-value", res.getHeaders().get( "some-header" ) );
        assertEquals( "component rendered", res.getBody() );
    }

    private void setupContent( final Content content )
    {
        this.request.setContent( content );
    }

    private Content createPageThatIsFragment( final Component fragmentComponent )
    {
        Page page = Page.create().fragment( fragmentComponent ).build();
        return createContentWithPage( page, ContentTypeName.fragment() );
    }

    private Content createPageWithFragment()
    {
        return createPageWithFragment( ContentId.from( "fragmentId" ) );
    }

    private Content createPageWithFragment( final ContentId fragmentId )
    {
        Regions regions = Regions.create()
            .add( Region.create().name( "main-region" ).add( FragmentComponent.create().fragment( fragmentId ).build() ).build() )
            .build();

        Page page = Page.create().template( PageTemplateKey.from( "my-page" ) ).regions( regions ).config( new PropertyTree() ).build();

        return createContentWithPage( page, ContentTypeName.from( "contentTypeName" ) );
    }

    final Content createContentWithPage( final Page page, final ContentTypeName contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        return Content.create()
            .id( ContentId.from( "id" ) )
            .path( ContentPath.from( "site/somepath/content" ) )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .displayName( "My Content" )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .type( contentTypeName )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                              .build() )
            .page( page )
            .build();
    }
}
