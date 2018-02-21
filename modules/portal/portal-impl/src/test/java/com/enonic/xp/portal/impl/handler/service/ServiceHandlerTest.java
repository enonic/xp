package com.enonic.xp.portal.impl.handler.service;

import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.region.ComponentName;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.service.ServiceDescriptorService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;

import static org.junit.Assert.*;

public class ServiceHandlerTest
    extends BaseHandlerTest
{
    private ServiceHandler handler;

    private PortalRequest request;

    protected ContentService contentService;

    protected ResourceService resourceService;

    protected ServiceDescriptorService serviceDescriptorService;

    private ControllerScript controllerScript;

    @Before
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        this.controllerScript = Mockito.mock( ControllerScript.class );
        Mockito.when( controllerScriptFactory.fromDir( Mockito.anyObject() ) ).thenReturn( this.controllerScript );

        final PortalResponse portalResponse = PortalResponse.create().build();
        Mockito.when( this.controllerScript.execute( Mockito.anyObject() ) ).thenReturn( portalResponse );

        this.resourceService = Mockito.mock( ResourceService.class );
        final Resource resourceNotFound = Mockito.mock( Resource.class );
        Mockito.when( resourceNotFound.exists() ).thenReturn( false );
        final Resource resource = Mockito.mock( Resource.class );
        Mockito.when( resource.exists() ).thenReturn( true );
        Mockito.when( this.resourceService.getResource( ResourceKey.from( "demo:/services/test" ) ) ).thenReturn( resourceNotFound );
        Mockito.when( this.resourceService.getResource( ResourceKey.from( "demo:/site/services/test" ) ) ).thenReturn( resource );

        this.serviceDescriptorService = Mockito.mock( ServiceDescriptorService.class );
        final DescriptorKey serviceDescriptorKey = DescriptorKey.from( "demo:test" );
        final ServiceDescriptor serviceDescriptor = ServiceDescriptor.create().
            key( serviceDescriptorKey ).
            build();
        Mockito.when( this.serviceDescriptorService.getByKey( serviceDescriptorKey ) ).thenReturn( serviceDescriptor );

        this.contentService = Mockito.mock( ContentService.class );

        this.handler = new ServiceHandler();
        this.handler.setControllerScriptFactory( controllerScriptFactory );
        this.handler.setContentService( this.contentService );
        this.handler.setResourceService( this.resourceService );
        this.handler.setServiceDescriptorService( this.serviceDescriptorService );

        this.request.setMethod( HttpMethod.GET );
        this.request.setContentPath( ContentPath.from( "/site/somepath/content" ) );
        this.request.setEndpointPath( "/_/service/demo/myservice" );
        this.request.setRawPath( "portal/draft/site/somepath/content/_/service/demo/myservice" );
    }

    @Test
    public void testOrder()
    {
        assertEquals( 0, this.handler.getOrder() );
    }

    @Test
    public void testMatch()
    {
        this.request.setEndpointPath( null );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/other/demo/myservice" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/service/demo/myservice" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/service/demo/myservice" );
        assertEquals( true, this.handler.canHandle( this.request ) );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        this.request.setMethod( HttpMethod.OPTIONS );
        final PortalResponse portalResponse = PortalResponse.create().status( HttpStatus.METHOD_NOT_ALLOWED ).build();
        Mockito.when( this.controllerScript.execute( Mockito.anyObject() ) ).thenReturn( portalResponse );
        this.request.setEndpointPath( "/_/service/demo/test" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,POST,HEAD,OPTIONS,PUT,DELETE,TRACE", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNotValidUrlPattern()
        throws Exception
    {
        this.request.setEndpointPath( "/_/service/" );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
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
        final ServiceDescriptor serviceDescriptor = ServiceDescriptor.create().
            key( serviceDescriptorKey ).
            setAllowedPrincipals( allowedPrincipals ).
            build();
        Mockito.when( this.serviceDescriptorService.getByKey( serviceDescriptorKey ) ).thenReturn( serviceDescriptor );

        this.request.setEndpointPath( "/_/service/demo/test" );

        boolean forbiddenErrorThrown = false;
        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
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
        this.request.setEndpointPath( "/_/service/demo/test" );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );

        Mockito.verify( this.controllerScript ).execute( this.request );

        assertNotNull( this.request.getApplicationKey() );
        assertNull( this.request.getSite() );
        assertNull( this.request.getContent() );
    }

    @Test
    public void executeScript_validSite()
        throws Exception
    {
        setupContentAndSite();

        this.request.setEndpointPath( "/_/service/demo/test" );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );

        Mockito.verify( this.controllerScript ).execute( this.request );

        assertNotNull( this.request.getApplicationKey() );
        assertNotNull( this.request.getSite() );
        assertNotNull( this.request.getContent() );
    }

    @Test(expected=com.enonic.xp.web.WebException.class)
    public void executeScript_invalidSite()
        throws Exception
    {
        setupContentAndSite();
        this.request.setEndpointPath( "/_/service/forbidden/test" );
        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
    }

    @Test
    public void executeScript_validApplication()
        throws Exception
    {
        this.request.setRawPath( "/app/demo/_/service/demo/test" );
        this.request.setEndpointPath( "/_/service/demo/test" );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );        
        Mockito.verify( this.controllerScript ).execute( this.request );
        assertNotNull( this.request.getApplicationKey() );
    }

    @Test(expected=com.enonic.xp.web.WebException.class)
    public void executeScript_invalidApplication()
        throws Exception
    {
        this.request.setRawPath( "/app/forbidden/_/service/demo/test" );
        this.request.setEndpointPath( "/_/service/demo/test" );

        final WebResponse response = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( HttpStatus.OK, response.getStatus() );        
        Mockito.verify( this.controllerScript ).execute( this.request );
        assertNotNull( this.request.getApplicationKey() );
    }

    private void setupContentAndSite()
        throws Exception
    {
        final Content content = createPage( "id", "site/somepath/content", "myapplication:ctype", true );

        Mockito.when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( content );

        Mockito.when( this.contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( createSite( "id", "site", "myapplication:contenttypename" ) );

        Mockito.when( this.contentService.getById( content.getId() ) ).
            thenReturn( content );
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
                    add( PartComponent.create().name( ComponentName.from( "mypart" ) ).
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

        final SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "demo" ) ).
            config( new PropertyTree() ).
            build();

        return Site.create().
            addSiteConfig( siteConfig ).
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
