package com.enonic.xp.portal.impl.handler.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.filter.FilterScript;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class MappingHandlerTest
{
    private MappingHandler handler;

    private PortalRequest request;

    protected ContentService contentService;

    protected ResourceService resourceService;

    private RendererDelegate rendererDelegate;

    private SiteService siteService;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        final ControllerScriptFactory controllerScriptFactory = mock( ControllerScriptFactory.class );
        ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScriptFactory.fromDir( Mockito.any() ) ).thenReturn( controllerScript );
        final PortalResponse portalResponse = PortalResponse.create().build();
        when( controllerScript.execute( Mockito.any() ) ).thenReturn( portalResponse );

        FilterScriptFactory filterScriptFactory = mock( FilterScriptFactory.class );
        FilterScript filterScript = mock( FilterScript.class );
        when( filterScriptFactory.fromScript( Mockito.any() ) ).thenReturn( filterScript );
        when( filterScript.execute( Mockito.any(), Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        this.resourceService = mock( ResourceService.class );
        final Resource resourceNotFound = mock( Resource.class );
        when( resourceNotFound.exists() ).thenReturn( false );
        final Resource resource = mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( this.resourceService.getResource( ResourceKey.from( "demo:/services/test" ) ) ).thenReturn( resource );

        this.contentService = mock( ContentService.class );
        this.rendererDelegate = mock( RendererDelegate.class );
        this.siteService = mock( SiteService.class );

        this.handler = new MappingHandler( resourceService, controllerScriptFactory, filterScriptFactory, rendererDelegate,
                                           new ControllerMappingsResolver( siteService ), new ContentResolver( contentService ) );
        this.request.setMethod( HttpMethod.GET );
    }

    @Test
    public void testOrder()
    {
        assertEquals( -10, this.handler.getOrder() );
    }

    @Test
    public void methodNotAllowed()
    {
        final PortalResponse response = PortalResponse.create().build();
        this.request.setBaseUri( "/admin/site" );
        this.request.setContentPath( ContentPath.from( "/site/content" ) );
        this.request.setMethod( HttpMethod.LOCK );
        final WebException webException = assertThrows( WebException.class, () -> this.handler.handle( this.request, response, null ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, webException.getStatus() );
    }

    @Test
    public void testNoMatch_no_site_based()
        throws Exception
    {
        final WebHandlerChain chain = mock( WebHandlerChain.class );
        final PortalResponse response = PortalResponse.create().build();
        this.request.setContentPath( ContentPath.from( "/site/content" ) );
        this.request.setBaseUri( "/something" );

        this.handler.handle( this.request, response, chain );
        verify( chain ).handle( this.request, response );
        verifyNoInteractions( rendererDelegate );
    }

    @Test
    public void testNoMatch_no_site()
        throws Exception
    {
        final WebHandlerChain chain = mock( WebHandlerChain.class );
        final PortalResponse response = PortalResponse.create().build();
        this.request.setBaseUri( "/admin/site" );
        this.request.setContentPath( ContentPath.from( "/site/content" ) );
        this.handler.handle( this.request, response, chain );
        verify( chain ).handle( this.request, response );
        verifyNoInteractions( rendererDelegate );
    }

    @Test
    public void testNoMatch_endpointPath()
        throws Exception
    {
        this.request.setEndpointPath( "something" );
        final WebHandlerChain chain = mock( WebHandlerChain.class );
        final PortalResponse response = PortalResponse.create().build();

        this.handler.handle( this.request, response, chain );
        verify( chain ).handle( this.request, response );
        verifyNoInteractions( rendererDelegate );
    }

    @Test
    public void executeNothing()
        throws Exception
    {
        final ResourceKey controller = ResourceKey.from( "demo:/services/test" );
        final ControllerMappingDescriptor mapping =
            ControllerMappingDescriptor.create().controller( controller ).pattern( "/nomatch" ).build();

        setupContentAndSite( mapping );

        final WebHandlerChain chain = mock( WebHandlerChain.class );
        final PortalResponse response = PortalResponse.create().build();

        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/site/somesite/content" ) );

        this.handler.handle( this.request, response, chain );

        verify( chain ).handle( this.request, response );
        verifyNoInteractions( rendererDelegate );
    }

    @Test
    public void executeScript()
        throws Exception
    {
        final ResourceKey controller = ResourceKey.from( "demo:/services/test" );
        final ControllerMappingDescriptor mapping =
            ControllerMappingDescriptor.create().controller( controller ).pattern( ".*/content" ).build();

        setupContentAndSite( mapping );
        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/site/somesite/content" ) );

        when( rendererDelegate.render( isA( ControllerMappingDescriptor.class ), same( request ) ) ).thenReturn(
            PortalResponse.create().body( "Ok" ).build() );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );

        assertNotNull( this.request.getApplicationKey() );
        assertNotNull( this.request.getSite() );
        assertNotNull( this.request.getContent() );
    }

    @Test
    public void executeFilter()
        throws Exception
    {
        final ResourceKey filter = ResourceKey.from( "demo:/services/test" );
        final ControllerMappingDescriptor mapping = ControllerMappingDescriptor.create().filter( filter ).pattern( ".*/content" ).build();

        setupContentAndSite( mapping );

        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/site/somesite/content" ) );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );

        assertNotNull( this.request.getApplicationKey() );
        assertNotNull( this.request.getSite() );
        assertNotNull( this.request.getContent() );
        assertEquals( "/site/draft/site", this.request.getContextPath() );
    }

    private void setupContentAndSite( final ControllerMappingDescriptor mapping )
    {
        final Content content = createPage( "id", "site/somesite/content", "myapplication:ctype", true );
        final Site site = createSite( "id", "site", "myapplication:contenttypename" );

        final ContentPath path = ContentPath.from( "site/somesite/content" ).asAbsolute();
        when( this.contentService.getByPath( path ) ).thenReturn( content );

        when( this.contentService.findNearestSiteByPath( eq( path ) ) ).thenReturn( site );

        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping );
        final SiteDescriptor siteDescriptor = SiteDescriptor.create().mappingDescriptors( mappings ).build();
        when( this.siteService.getDescriptor( any( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
    }

    private Content createPage( final String id, final String path, final String contentTypeName, final boolean withPage )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        final Content.Builder content = Content.create()
            .id( ContentId.from( id ) )
            .path( ContentPath.from( path ) )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .displayName( "My Content" )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .type( ContentTypeName.from( contentTypeName ) );

        if ( withPage )
        {
            PageRegions pageRegions = PageRegions.create()
                .add( Region.create().name( "main-region" ).add( PartComponent.create().descriptor( "myapp:mypart" ).build() ).build() )
                .build();

            Page page = Page.create().template( PageTemplateKey.from( "my-page" ) ).regions( pageRegions ).config( rootDataSet ).build();
            content.page( page );
        }
        return content.build();
    }

    private Site createSite( final String id, final String path, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Page page = Page.create().template( PageTemplateKey.from( "my-page" ) ).config( rootDataSet ).build();

        final SiteConfig siteConfig =
            SiteConfig.create().application( ApplicationKey.from( "myapplication" ) ).config( new PropertyTree() ).build();
        return Site.create()
            .siteConfigs( SiteConfigs.from( siteConfig ) )
            .id( ContentId.from( id ) )
            .path( ContentPath.from( path ) )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .displayName( "My Content" )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .type( ContentTypeName.from( contentTypeName ) )
            .page( page )
            .build();
    }
}
