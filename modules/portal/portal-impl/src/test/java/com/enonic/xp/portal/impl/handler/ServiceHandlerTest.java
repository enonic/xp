package com.enonic.xp.portal.impl.handler;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
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
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.service.ServiceDescriptorService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceHandlerTest
{
    private ServiceHandler handler;

    private PortalRequest request;

    protected ContentService contentService;

    protected ResourceService resourceService;

    protected ServiceDescriptorService serviceDescriptorService;

    private ControllerScript controllerScript;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        final ControllerScriptFactory controllerScriptFactory = mock( ControllerScriptFactory.class );
        this.controllerScript = mock( ControllerScript.class );
        when( controllerScriptFactory.fromScript( Mockito.any() ) ).thenReturn( this.controllerScript );

        final PortalResponse portalResponse = PortalResponse.create().build();
        when( this.controllerScript.execute( Mockito.any() ) ).thenReturn( portalResponse );

        this.resourceService = mock( ResourceService.class );
        final Resource resourceNotFound = mock( Resource.class );
        when( resourceNotFound.exists() ).thenReturn( false );
        final Resource resource = mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( this.resourceService.getResource( ResourceKey.from( "demo:/services/test" ) ) ).thenReturn( resourceNotFound );

        this.serviceDescriptorService = mock( ServiceDescriptorService.class );
        final DescriptorKey serviceDescriptorKey = DescriptorKey.from( "demo:test" );
        final ServiceDescriptor serviceDescriptor = ServiceDescriptor.create().key( serviceDescriptorKey ).build();
        when( this.serviceDescriptorService.getByKey( serviceDescriptorKey ) ).thenReturn( serviceDescriptor );

        this.contentService = mock( ContentService.class );
        ProjectService projectService = mock( ProjectService.class );

        this.handler = new ServiceHandler( contentService, serviceDescriptorService, controllerScriptFactory, projectService );

        this.request.setMethod( HttpMethod.GET );
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setEndpointPath( "/_/service/demo/myservice" );
        this.request.setRawPath( "/site/draft/site/somepath/content/_/service/demo/myservice" );
        this.request.setBaseUri( "/site" );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        this.request.setMethod( HttpMethod.OPTIONS );
        final PortalResponse portalResponse = PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        when( this.controllerScript.execute( Mockito.any() ) ).thenReturn( portalResponse );
        this.request.setEndpointPath( "/_/service/demo/test" );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE,PATCH", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNotValidUrlPattern()
        throws Exception
    {
        this.request.setEndpointPath( "/_/service/" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Not a valid service url pattern", e.getMessage() );
        }
    }

    @Test
    public void testForbiddenService()
        throws Exception
    {
        final DescriptorKey serviceDescriptorKey = DescriptorKey.from( "demo:test" );
        final Set<PrincipalKey> allowedPrincipals = Collections.singleton( PrincipalKey.from( "role:system.admin" ) );
        final ServiceDescriptor serviceDescriptor =
            ServiceDescriptor.create().key( serviceDescriptorKey ).setAllowedPrincipals( allowedPrincipals ).build();
        when( this.serviceDescriptorService.getByKey( serviceDescriptorKey ) ).thenReturn( serviceDescriptor );

        this.request.setEndpointPath( "/_/service/demo/test" );

        boolean forbiddenErrorThrown = false;
        try
        {
            this.handler.handle( this.request );
        }
        catch ( WebException e )
        {
            if ( HttpStatus.UNAUTHORIZED == e.getStatus() )
            {
                forbiddenErrorThrown = true;
            }
        }
        assertTrue( forbiddenErrorThrown );
    }

    @Test
    public void executeScript_noContent()
        throws Exception
    {
        this.request.setBaseUri( "/webapp/demo" );
        this.request.setEndpointPath( "/_/service/demo/test" );

        final WebResponse response = this.handler.handle( this.request );
        assertEquals( HttpStatus.OK, response.getStatus() );

        verify( this.controllerScript ).execute( this.request );

        assertNotNull( this.request.getApplicationKey() );
        assertNull( this.request.getSite() );
        assertNull( this.request.getContent() );
    }

    @Test
    public void executeScript_validSite()
        throws Exception
    {
        setupContentAndSite();

        this.request.setRepositoryId( RepositoryId.from( "com.enonic.cms.myrepo" ) );
        this.request.setEndpointPath( "/_/service/demo/test" );

        final WebResponse response = this.handler.handle( this.request );
        assertEquals( HttpStatus.OK, response.getStatus() );

        verify( this.controllerScript ).execute( this.request );

        assertNotNull( this.request.getApplicationKey() );
        assertNotNull( this.request.getSite() );
        assertNotNull( this.request.getContent() );
        assertEquals( "/site/draft/site/somepath/content/_/service/demo/test", this.request.getContextPath() );
    }

    @Test
    public void executeScript_invalidSite()
    {
        setupContentAndSite();
        this.request.setEndpointPath( "/_/service/forbidden/test" );
        assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
    }

    @Test
    public void executeScript_validApplication()
        throws Exception
    {
        this.request.setBaseUri( "/webapp/demo" );
        this.request.setRawPath( "/webapp/demo/_/service/demo/test" );
        this.request.setEndpointPath( "/_/service/demo/test" );

        final WebResponse response = this.handler.handle( this.request );
        assertEquals( HttpStatus.OK, response.getStatus() );
        verify( this.controllerScript ).execute( this.request );
        assertNotNull( this.request.getApplicationKey() );
    }

    @Test
    public void executeScript_invalidApplication()
    {
        this.request.setBaseUri( "/webapp/forbidden" );
        this.request.setRawPath( "/webapp/forbidden/_/service/demo/test" );
        this.request.setEndpointPath( "/_/service/demo/test" );

        assertThrows( WebException.class, () -> {
            final WebResponse response = this.handler.handle( this.request );
            assertEquals( HttpStatus.OK, response.getStatus() );
            verify( this.controllerScript ).execute( this.request );
            assertNotNull( this.request.getApplicationKey() );
        } );
    }

    @Test
    void testHandleMethodNotAllowed()
    {
        this.request.setMethod( HttpMethod.CONNECT );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method CONNECT not allowed", ex.getMessage() );
    }

    private void setupContentAndSite()
    {
        final Content content = createPage( "id", "site/somepath/content", "myapplication:ctype", true );

        when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).thenReturn( content );

        final Site site = createSite( "id", "site", "myapplication:contenttypename" );
        when( this.contentService.getNearestSite( isA( ContentId.class ) ) ).thenReturn( site );

        when( this.contentService.findNearestSiteByPath( isA( ContentPath.class ) ) ).thenReturn( site );

        when( this.contentService.getById( content.getId() ) ).thenReturn( content );
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
            .type( ContentTypeName.from( contentTypeName ) )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                              .build() );

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

        final SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "demo" ) ).config( new PropertyTree() ).build();

        return Site.create()
            .addSiteConfig( siteConfig )
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
