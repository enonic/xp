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
import com.enonic.xp.portal.impl.rendering.Renderer;
import com.enonic.xp.portal.impl.rendering.RendererFactory;
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
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class MappingHandlerTest
    extends BaseHandlerTest
{
    private MappingHandler handler;

    private PortalRequest request;

    protected ContentService contentService;

    protected ResourceService resourceService;

    private ControllerScript controllerScript;

    private RendererFactory rendererFactory;

    private FilterScriptFactory filterScriptFactory;

    private FilterScript filterScript;

    private SiteService siteService;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        this.controllerScript = Mockito.mock( ControllerScript.class );
        when( controllerScriptFactory.fromDir( Mockito.any() ) ).thenReturn( this.controllerScript );
        final PortalResponse portalResponse = PortalResponse.create().build();
        when( this.controllerScript.execute( Mockito.any() ) ).thenReturn( portalResponse );

        this.filterScriptFactory = Mockito.mock( FilterScriptFactory.class );
        this.filterScript = Mockito.mock( FilterScript.class );
        when( filterScriptFactory.fromScript( Mockito.any() ) ).thenReturn( this.filterScript );
        when( this.filterScript.execute( Mockito.any(), Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        this.resourceService = Mockito.mock( ResourceService.class );
        final Resource resourceNotFound = Mockito.mock( Resource.class );
        when( resourceNotFound.exists() ).thenReturn( false );
        final Resource resource = Mockito.mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( this.resourceService.getResource( ResourceKey.from( "demo:/services/test" ) ) ).thenReturn( resource );

        this.contentService = Mockito.mock( ContentService.class );
        this.rendererFactory = Mockito.mock( RendererFactory.class );
        this.siteService = Mockito.mock( SiteService.class );

        this.handler = new MappingHandler();
        this.handler.setControllerScriptFactory( controllerScriptFactory );
        this.handler.setContentService( this.contentService );
        this.handler.setResourceService( this.resourceService );
        this.handler.setRendererFactory( this.rendererFactory );
        this.handler.setSiteService( this.siteService );
        this.handler.setFilterScriptFactory( this.filterScriptFactory );

        this.request.setMethod( HttpMethod.GET );
    }

    @Test
    public void testOrder()
    {
        assertEquals( -10, this.handler.getOrder() );
    }

    @Test
    public void testNoMatch()
        throws Exception
    {
        this.request.setEndpointPath( null );
        assertEquals( false, this.handler.canHandle( this.request ) );
    }

    @Test
    public void testMatch()
        throws Exception
    {
        final ResourceKey controller = ResourceKey.from( "demo:/services/test" );
        final ControllerMappingDescriptor mapping = ControllerMappingDescriptor.create().
            controller( controller ).
            pattern( ".*/content" ).
            build();
        setupContentAndSite( mapping );
        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setSite( this.contentService.getNearestSite( ContentId.from( "id" ) ) );
        this.request.setEndpointPath( "" );
        assertEquals( true, this.handler.canHandle( this.request ) );
    }

    @Test
    public void executeScript()
        throws Exception
    {
        final ResourceKey controller = ResourceKey.from( "demo:/services/test" );
        final ControllerMappingDescriptor mapping = ControllerMappingDescriptor.create().
            controller( controller ).
            pattern( ".*/content" ).
            build();

        setupContentAndSite( mapping );
        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setSite( this.contentService.getNearestSite( ContentId.from( "id" ) ) );
        this.request.setEndpointPath( "" );
        this.request.setContent( this.contentService.getById( ContentId.from( "id" ) ) );

        Renderer<ControllerMappingDescriptor> renderer = new Renderer<>()
        {
            @Override
            public Class<ControllerMappingDescriptor> getType()
            {
                return ControllerMappingDescriptor.class;
            }

            @Override
            public PortalResponse render( final ControllerMappingDescriptor component, final PortalRequest portalRequest )
            {
                return PortalResponse.create().body( "Ok" ).build();
            }
        };

        when( rendererFactory.getRenderer( isA( ControllerMappingDescriptor.class ) ) ).thenReturn( renderer );

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
        final ControllerMappingDescriptor mapping = ControllerMappingDescriptor.create().
            filter( filter ).
            pattern( ".*/content" ).
            build();

        setupContentAndSite( mapping );

        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setSite( this.contentService.getNearestSite( ContentId.from( "id" ) ) );
        this.request.setEndpointPath( "" );
        this.request.setContent( this.contentService.getById( ContentId.from( "id" ) ) );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );

        assertNotNull( this.request.getApplicationKey() );
        assertNotNull( this.request.getSite() );
        assertNotNull( this.request.getContent() );
        assertEquals( "/site/draft/site", this.request.getContextPath() );
    }

    private void setupContentAndSite( final ControllerMappingDescriptor mapping )
        throws Exception
    {
        final Content content = createPage( "id", "site/somepath/content", "myapplication:ctype", true );
        final Site site = createSite( "id", "site", "myapplication:contenttypename" );

        when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).thenReturn( content );

        when( this.contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).thenReturn( site );

        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping );
        final SiteDescriptor siteDescriptor = SiteDescriptor.create().mappingDescriptors( mappings ).build();
        when( this.siteService.getDescriptor( any( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
    }

    private Content createPage( final String id, final String path, final String contentTypeName, final boolean withPage )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        final Content.Builder content = Content.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) );

        if ( withPage )
        {
            PageRegions pageRegions = PageRegions.create().
                add( Region.create().name( "main-region" ).
                    add( PartComponent.create().descriptor( "myapp:mypart" ).
                        build() ).
                    build() ).
                build();

            Page page = Page.create().
                template( PageTemplateKey.from( "my-page" ) ).
                regions( pageRegions ).
                config( rootDataSet ).
                build();
            content.page( page );
        }
        return content.build();
    }

    private Site createSite( final String id, final String path, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Page page = Page.create().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            build();

        final SiteConfig siteConfig =
            SiteConfig.create().application( ApplicationKey.from( "myapplication" ) ).config( new PropertyTree() ).build();
        return Site.create().
            siteConfigs( SiteConfigs.from( siteConfig ) ).
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }
}
