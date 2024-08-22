package com.enonic.xp.portal.impl.handler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.resource.MockResource;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssetHandlerTest
{
    private AssetHandler handler;

    protected PortalRequest request;

    private Map<Object, Resource> resources;

    private Resource nullResource;

    ResourceService resourceService;

    HttpServletRequest req;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        this.resources = new HashMap<>();

        resourceService = mock( ResourceService.class );
        when( resourceService.getResource( Mockito.any() ) ).then( this::getResource );

        this.handler = new AssetHandler( resourceService );

        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.asset_legacyContextPath() ).thenReturn( true );
        this.handler.activate( portalConfig );

        this.nullResource = mock( Resource.class );
        when( this.nullResource.exists() ).thenReturn( false );

        this.request.setBaseUri( "/site" );
        this.request.setMethod( HttpMethod.GET );
        this.request.setEndpointPath( "/_/asset/demo/css/main.css" );

        req = mock( HttpServletRequest.class );
        this.request.setRawRequest( req );
    }

    private Resource addResource( final String key )
        throws Exception
    {
        final ResourceKey resourceKey = ResourceKey.from( key );

        final Resource resource = mock( Resource.class );
        when( resource.exists() ).thenReturn( true );
        when( resource.getKey() ).thenReturn( resourceKey );

        this.resources.put( resourceKey, resource );
        return resource;
    }

    private Resource getResource( final InvocationOnMock invocation )
    {
        final Resource res = this.resources.get( invocation.getArguments()[0] );
        return res != null ? res : this.nullResource;
    }

    @Test
    public void testOptions()
        throws Exception
    {
        addResource( "demo:/assets/css/main.css" );

        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testSiteResourceNotFound()
        throws Exception
    {
        addResource( "demo:/site/assets/css/main.css" );

        final WebException ex = assertThrows( WebException.class, () -> {
            this.handler.handle( this.request );
        } );
        assertEquals( "Resource [demo:/assets/css/main.css] not found", ex.getMessage() );
    }

    @Test
    public void testRootResourceFound()
        throws Exception
    {
        final Resource resource = addResource( "demo:/assets/css/main.css" );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.CSS_UTF_8.withoutParameters(), res.getContentType() );
        assertSame( resource, res.getBody() );
    }

    @Test
    public void testResourceNotFound()
        throws Exception
    {
        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Resource [demo:/assets/css/main.css] not found", e.getMessage() );
        }
    }

    @Test
    public void testNotValidUrlPattern()
        throws Exception
    {
        this.request.setEndpointPath( "/_/asset/" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Not a valid asset url pattern", e.getMessage() );
        }
    }

    @Test
    public void testCacheHeader_FingerprintDoesntMatch()
        throws Exception
    {
        addResource( "demo:/assets/css/main.css" );
        this.request.setEndpointPath( "/_/asset/demo:123/css/main.css" );

        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "demo" ), "META-INF/MANIFEST.MF" );
        when( this.resourceService.getResource( resourceKey ) ).thenReturn( MockResource.empty( resourceKey, 1 ) );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertNull( res.getHeaders().get( "Cache-Control" ) );
    }

    @Test
    public void testCacheHeader()
        throws Exception
    {
        addResource( "demo:/assets/css/main.css" );
        this.request.setEndpointPath( "/_/asset/demo:0000000000000001/css/main.css" );

        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "demo" ), "META-INF/MANIFEST.MF" );
        when( this.resourceService.getResource( resourceKey ) ).thenReturn( MockResource.empty( resourceKey, 1 ) );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "public, max-age=31536000, immutable", res.getHeaders().get( "Cache-Control" ) );
    }

    @Test
    public void testContextPathConfiguration()
        throws Exception
    {
        VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/master/mysite" );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.asset_legacyContextPath() ).thenReturn( false );

        this.handler.activate( portalConfig );

        addResource( "demo:/assets/css/main.css" );
        this.request.setEndpointPath( "/_/asset/demo:0000000000000001/css/main.css" );
        this.request.setRawPath( "/site/myproject/master/mysite/_/asset/demo:0000000000000001/css/main.css" );

        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "demo" ), "META-INF/MANIFEST.MF" );
        when( this.resourceService.getResource( resourceKey ) ).thenReturn( MockResource.empty( resourceKey, 1 ) );

        WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );

        // test invalid context path
        this.request.setRawPath( "/admin/tool/path/_/asset/demo:0000000000000001/css/main.css" );
        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    public void testAssetOnAdminContextPath()
        throws Exception
    {
        VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/master/mysite" );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.asset_legacyContextPath() ).thenReturn( false );
        this.handler.activate( portalConfig );

        addResource( "demo:/assets/css/main.css" );
        this.request.setRawPath( "/admin/_/asset/demo:0000000000000001/css/main.css" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    public void testNoCacheHeader()
        throws Exception
    {
        addResource( "demo:/assets/css/main.css" );

        this.request.setMode( RenderMode.EDIT );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertNull( res.getHeaders().get( "Cache-Control" ) );
    }

    @Test
    void testHandleMethodNotAllowed()
    {
        this.request.setMethod( HttpMethod.DELETE );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method DELETE not allowed", ex.getMessage() );
    }
}
