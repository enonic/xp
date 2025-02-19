package com.enonic.xp.portal.impl.handler.mapping;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.filter.FilterScript;
import com.enonic.xp.portal.filter.FilterScriptFactory;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    private ProjectService projectService;

    private PageTemplateService pageTemplateService;

    private PageDescriptorService pageDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        this.request.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        final ControllerScriptFactory controllerScriptFactory = mock( ControllerScriptFactory.class );
        ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScriptFactory.fromScript( Mockito.any() ) ).thenReturn( controllerScript );
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
        this.projectService = mock( ProjectService.class );
        this.pageTemplateService = mock( PageTemplateService.class );
        this.pageDescriptorService = mock( PageDescriptorService.class );
        this.layoutDescriptorService = mock( LayoutDescriptorService.class );

        this.handler = new MappingHandler( siteService, contentService, resourceService, controllerScriptFactory, filterScriptFactory,
                                           rendererDelegate, projectService, pageTemplateService, pageDescriptorService,
                                           layoutDescriptorService );
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
        this.request.setBaseUri( "/site" );
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

        setupContentAndSite( mapping, false );

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

        setupContentAndSite( mapping, false );

        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/site/somesite/content" ) );

        when( rendererDelegate.render( isA( ControllerMappingDescriptor.class ), same( request ) ) ).thenReturn(
            PortalResponse.create().body( "Ok body" ).build() );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "Ok body", response.getBody() );
    }

    @Test
    public void executeScriptFromProjectApp()
        throws Exception
    {
        final ResourceKey controller = ResourceKey.from( "demo:/services/test" );

        final ControllerMappingDescriptor projectMapping =
            ControllerMappingDescriptor.create().controller( controller ).pattern( ".*/content" ).build();

        final String contentPath = "/some/path/content";
        setupContentAndSiteAndProject( projectMapping, null, contentPath );

        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( contentPath ) );

        when( rendererDelegate.render( eq( projectMapping ), same( request ) ) ).thenReturn(
            PortalResponse.create().body( "Project body" ).build() );
        final Site site = createSite( "id", "mysite", "myapplication:contenttypename", "project-app1" );
        when( this.contentService.findNearestSiteByPath( any( ContentPath.class) ) ).thenReturn( site );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "Project body", response.getBody() );

        verify( rendererDelegate, Mockito.times( 1 ) ).render( isA( ControllerMappingDescriptor.class ), same( request ) );
    }

    @Test
    public void executeScriptFromSiteOverProject()
        throws Exception
    {
        final Resource resource = mock( Resource.class );
        when( resource.exists() ).thenReturn( true );

        final ResourceKey controller1 = ResourceKey.from( "demo:/services/test1" );
        final ResourceKey controller2 = ResourceKey.from( "demo:/services/test2" );

        when( this.resourceService.getResource( controller1 ) ).thenReturn( resource );
        when( this.resourceService.getResource( controller2 ) ).thenReturn( resource );

        final ControllerMappingDescriptor siteMapping =
            ControllerMappingDescriptor.create().controller( controller1 ).pattern( ".*/content" ).build();

        final ControllerMappingDescriptor projectMapping =
            ControllerMappingDescriptor.create().controller( controller2 ).pattern( ".*/content" ).build();

        final String contentPath = "/site/somesite/content";

        setupContentAndSiteAndProject( projectMapping, siteMapping, contentPath );

        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( contentPath ) );

        when( rendererDelegate.render( eq( siteMapping ), same( request ) ) ).thenReturn(
            PortalResponse.create().body( "Site body" ).build() );

        when( rendererDelegate.render( eq( projectMapping ), same( request ) ) ).thenReturn(
            PortalResponse.create().body( "Project body" ).build() );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );
        assertEquals( "Site body", response.getBody() );

        verify( rendererDelegate, Mockito.times( 1 ) ).render( isA( ControllerMappingDescriptor.class ), same( request ) );
    }

    @Test
    public void executeFilter()
        throws Exception
    {
        final ResourceKey filter = ResourceKey.from( "demo:/services/test" );
        final ControllerMappingDescriptor mapping = ControllerMappingDescriptor.create().filter( filter ).pattern( ".*/content" ).build();

        setupContentAndSite( mapping, false );

        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/site/somesite/content" ) );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );

        assertEquals( ApplicationKey.from( "demo" ), this.request.getApplicationKey() );
        assertNull( this.request.getPageDescriptor() );
        assertNotNull( this.request.getSite() );
        assertNotNull( this.request.getContent() );
        assertEquals( "/site/myproject/draft/site", this.request.getContextPath() );
    }

    @Test
    void executeFilter_withPage()
        throws Exception
    {
        final ResourceKey filter = ResourceKey.from( "demo:/services/test" );
        final ControllerMappingDescriptor mapping = ControllerMappingDescriptor.create().filter( filter ).pattern( ".*/content" ).build();

        setupContentAndSite( mapping, true );

        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/site/somesite/content" ) );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );

        assertEquals( ApplicationKey.from( "demo" ), this.request.getApplicationKey() );
        assertEquals( DescriptorKey.from( "module:landing-page" ), this.request.getPageDescriptor().getKey() );
        assertNotNull( this.request.getSite() );
        assertNotNull( this.request.getContent() );
        assertEquals( "/site/default/draft/site", this.request.getContextPath() );
    }

    private void setupContentAndSite( final ControllerMappingDescriptor mapping, boolean withPage )
    {
        final Content content = createPage( "id", "site/somesite/content", "myapplication:ctype", withPage );
        final Site site = createSite( "id", "site", "myapplication:contenttypename", "myapplication" );

        final ContentPath path = ContentPath.from( "site/somesite/content" ).asAbsolute();
        when( this.contentService.getByPath( path ) ).thenReturn( content );

        when( this.contentService.findNearestSiteByPath( eq( path ) ) ).thenReturn( site );

        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final PageTemplate pageTemplate = createPageTemplate();

        when( this.pageTemplateService.getByKey( any( PageTemplateKey.class ) ) ).thenReturn( pageTemplate );
        when( pageDescriptorService.getByKey( pageTemplate.getController() ) ).thenReturn( createPageDescriptor() );

        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping );
        final SiteDescriptor siteDescriptor = SiteDescriptor.create().mappingDescriptors( mappings ).build();
        when( this.siteService.getDescriptor( any( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );
    }

    private void setupContentAndSiteAndProject( final ControllerMappingDescriptor projectMapping,
                                                final ControllerMappingDescriptor siteMapping, final String contentPath )
    {
        final Content content = createPage( "id", contentPath, "app1:ctype", true );

        final Project project = createProject( "my-project", ApplicationKeys.from( ApplicationKey.from( "project-app1" ),
                                                                                   ApplicationKey.from( "project-app2" ) ) );

        when( projectService.get( isA( ProjectName.class ) ) ).then( ( answer ) -> {
            assertTrue( ContextAccessor.current().getAuthInfo().hasRole( RoleKeys.ADMIN ) );

            return project;
        } );

        this.request.setRepositoryId( project.getName().getRepoId() );

        final ContentPath path = ContentPath.from( contentPath ).asAbsolute();

        when( this.contentService.getByPath( path ) ).thenReturn( content );
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        when( this.pageTemplateService.getByKey( any( PageTemplateKey.class ) ) ).thenReturn( createPageTemplate() );

        if ( siteMapping != null )
        {
            final Site site = createSite( "id", "mysite", "myapplication:contenttypename", "myapplication" );
            when( this.contentService.findNearestSiteByPath( eq( path ) ) ).thenReturn( site );

            final ControllerMappingDescriptors siteMappings = ControllerMappingDescriptors.from( siteMapping );
            final SiteDescriptor siteDescriptor = SiteDescriptor.create().mappingDescriptors( siteMappings ).build();
            when( this.siteService.getDescriptor( eq( ApplicationKey.from( "myapplication" ) ) ) ).thenReturn( siteDescriptor );
        }

        if ( projectMapping != null )
        {
            final ControllerMappingDescriptors projectMappings = ControllerMappingDescriptors.from( projectMapping );
            final SiteDescriptor projectDescriptor = SiteDescriptor.create().mappingDescriptors( projectMappings ).build();
            when( this.siteService.getDescriptor( eq( ApplicationKey.from( "project-app1" ) ) ) ).thenReturn( projectDescriptor );
        }
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
            .permissions( AccessControlList.create()
                               .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                               .build() )
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

    private Site createSite( final String id, final String path, final String contentTypeName, final String appKey )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Page page = Page.create().template( PageTemplateKey.from( "my-page" ) ).config( rootDataSet ).build();

        final SiteConfig siteConfig =
            SiteConfig.create().application( ApplicationKey.from( appKey ) ).config( new PropertyTree() ).build();
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

    private Project createProject( final String name, final ApplicationKeys applicationKeys )
    {
        final Project.Builder project = Project.create().name( ProjectName.from( name ) ).displayName( name ).description( name );

        Optional.ofNullable( applicationKeys )
            .ifPresent( s -> s.stream()
                .map( applicationKey -> SiteConfig.create().application( applicationKey ).config( new PropertyTree() ).build() )
                .forEach( project::addSiteConfig ) );

        return project.build();
    }

    private PageTemplate createPageTemplate()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( "otherapp:my-template-controller" );
        final PageTemplate.Builder pageTemplate = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "my-page-tempalte" ) )
            .controller( descriptorKey )
            .id( ContentId.from( "pageTemplateId" ) )
            .path( ContentPath.from( "site/somesite/template" ) )
            .type( ContentTypeName.pageTemplate() );
        return pageTemplate.build();
    }

    private static PageDescriptor createPageDescriptor()
    {
        return PageDescriptor.create()
            .displayName( "Landing page" )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() )
            .key( DescriptorKey.from( "module:landing-page" ) )
            .build();
    }
}
