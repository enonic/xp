package com.enonic.xp.portal.impl.handler.mapping;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.filter.FilterScript;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ComponentServiceMappingHandlerTest
    extends BaseHandlerTest
{
    private ComponentServiceMappingHandler handler;

    private ContentService contentService;

    private ResourceService resourceService;

    private SiteService siteService;

    private PortalRequest request;

    private WebHandlerChain webHandlerChain;

    private FilterScript filterScript;

    @BeforeEach
    final void setup()
    {
        this.request = new PortalRequest();
        this.contentService = mock( ContentService.class );
        ProjectService projectService = mock( ProjectService.class );
        this.resourceService = mock( ResourceService.class );
        RendererDelegate rendererDelegate = mock( RendererDelegate.class );
        this.siteService = mock( SiteService.class );
        this.webHandlerChain = mock( WebHandlerChain.class );

        final ControllerScriptFactory controllerScriptFactory = mock( ControllerScriptFactory.class );
        ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScriptFactory.fromScript( Mockito.any() ) ).thenReturn( controllerScript );
        final PortalResponse portalResponse = PortalResponse.create().build();
        when( controllerScript.execute( Mockito.any() ) ).thenReturn( portalResponse );

        FilterScriptFactory filterScriptFactory = mock( FilterScriptFactory.class );
        filterScript = mock( FilterScript.class );
        when( filterScriptFactory.fromScript( Mockito.any() ) ).thenReturn( filterScript );
        when( filterScript.execute( Mockito.any(), Mockito.any(), Mockito.any() ) ).thenReturn( portalResponse );

        this.handler = new ComponentServiceMappingHandler( projectService, resourceService, controllerScriptFactory, filterScriptFactory,
                                                           rendererDelegate, siteService, contentService );

        this.request.setMethod( HttpMethod.GET );
        this.request.setBranch( ContentConstants.BRANCH_MASTER );
        this.request.setBaseUri( "/site" );
        this.request.setEndpointPath( "/_/component/123456/scale-100-100/image-name.jpg" );
    }

    private void setupContentInsideSite( final SiteConfigs siteConfigs )
    {
        final Site site = Site.create().name( "my-site" ).parentPath( ContentPath.ROOT ).siteConfigs( siteConfigs ).build();
        final Content content = Content.create().name( "my-content" ).parentPath( site.getPath() ).build();

        when( this.contentService.getById( eq( site.getId() ) ) ).thenReturn( site );
        when( this.contentService.getByPath( eq( site.getPath() ) ) ).thenReturn( site );

        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( this.contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );
        when( this.contentService.findNearestSiteByPath( eq( content.getPath() ) ) ).thenReturn( site );

        this.request.setContentPath( content.getPath() );
    }

    @Test
    void canHandle()
    {
        this.request.setEndpointPath( null );
        assertFalse( this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/other/123456/component" );
        assertFalse( this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/component/123456/component" );
        assertFalse( this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/component/123456/component" );
        assertTrue( this.handler.canHandle( this.request ) );
    }

    @Test
    void notValidUrlPattern()
        throws Exception
    {
        this.request.setEndpointPath( "/_/component/" );

        this.handler.handle( this.request, PortalResponse.create().build(), webHandlerChain );
        verify( webHandlerChain, times( 1 ) ).handle( eq( request ), isA( PortalResponse.class ) );
    }

    @Test
    void filterFromSiteConfig()
        throws Exception
    {
        final Resource resource = mock( Resource.class );
        when( resource.exists() ).thenReturn( true );

        final ResourceKey filter = ResourceKey.from( "demo:/services/test1" );

        when( this.resourceService.getResource( filter ) ).thenReturn( resource );

        final ControllerMappingDescriptor siteMapping =
            ControllerMappingDescriptor.create().filter( filter ).service( "component" ).build();

        final ControllerMappingDescriptors siteMappings = ControllerMappingDescriptors.from( siteMapping );
        final SiteDescriptor siteDescriptor = SiteDescriptor.create().mappingDescriptors( siteMappings ).build();
        final ApplicationKey myapplication = ApplicationKey.from( "myapplication" );
        when( this.siteService.getDescriptor( eq( myapplication ) ) ).thenReturn( siteDescriptor );

        final String body = "Project body";

        when( filterScript.execute( eq( request ), isA( PortalResponse.class ), eq( webHandlerChain ) ) ).thenReturn(
            PortalResponse.create().body( body ).build() );

        setupContentInsideSite( SiteConfigs.from( SiteConfig.create().application( myapplication ).config( new PropertyTree() ).build() ) );

        this.request.setEndpointPath( "/_/component/path/to/component" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), webHandlerChain );

        assertEquals( body, res.getBody() );
        verify( webHandlerChain, never() ).handle( eq( request ), isA( PortalResponse.class ) );

    }

    @Test
    void wrongServiceType()
        throws Exception
    {
        final ResourceKey controller1 = ResourceKey.from( "demo:/services/test1" );

        final ControllerMappingDescriptor siteMapping =
            ControllerMappingDescriptor.create().controller( controller1 ).service( "not-component" ).build();

        final ControllerMappingDescriptors siteMappings = ControllerMappingDescriptors.from( siteMapping );
        final SiteDescriptor siteDescriptor = SiteDescriptor.create().mappingDescriptors( siteMappings ).build();
        final ApplicationKey myapplication = ApplicationKey.from( "myapplication" );

        when( this.siteService.getDescriptor( eq( myapplication ) ) ).thenReturn( siteDescriptor );

        setupContentInsideSite( SiteConfigs.from( SiteConfig.create().application( myapplication ).config( new PropertyTree() ).build() ) );

        this.request.setEndpointPath( "/_/component/path/to/component" );

        this.handler.handle( this.request, PortalResponse.create().build(), webHandlerChain );

        verify( webHandlerChain, times( 1 ) ).handle( eq( request ), isA( PortalResponse.class ) );
    }

}
