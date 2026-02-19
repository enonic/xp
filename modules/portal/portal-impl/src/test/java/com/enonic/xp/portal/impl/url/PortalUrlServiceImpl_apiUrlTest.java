package com.enonic.xp.portal.impl.url;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.Site;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalUrlServiceImpl_apiUrlTest
{
    private PortalUrlService service;

    private PortalRequest portalRequest;

    private HttpServletRequest req;

    @BeforeEach
    void setUp()
    {
        PortalUrlGeneratorService portalUrlGeneratorService = new PortalUrlGeneratorServiceImpl();

        this.service = new PortalUrlServiceImpl( mock( ContentService.class ), mock( ResourceService.class ), mock( MacroService.class ),
                                                 mock( StyleDescriptorService.class ), mock( RedirectChecksumService.class ),
                                                 mock( ProjectService.class ), portalUrlGeneratorService );

        req = mock( HttpServletRequest.class );

        portalRequest = new PortalRequest();
        portalRequest.setRawRequest( req );

        PortalRequestAccessor.set( portalRequest );
    }

    @AfterEach
    void destroy()
    {
        PortalRequestAccessor.remove();
    }

    @Test
    void testNoRequest()
    {
        PortalRequestAccessor.set( null );

        final ApiUrlParams params =
            ApiUrlParams.create().setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) ).setPath( "path" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/api/com.enonic.app.myapp:myapi/path", url );
    }

    @Test
    void testNoRequestWithBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final ApiUrlParams params = ApiUrlParams.create()
            .setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) )
            .setPath( "path" )
            .setBaseUrl( "baseUrl" )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "baseUrl/_/com.enonic.app.myapp:myapi/path", url );
    }

    @Test
    void testSiteRequest()
    {
        final ContentPath contentPath = ContentPath.from( "sitePath" );

        portalRequest.setBaseUri( "/site" );
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setContentPath( contentPath );
        portalRequest.setApplicationKey( ApplicationKey.from( "com.enonic.app.myapp" ) );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( contentPath );
        portalRequest.setSite( site );

        final ApiUrlParams params = ApiUrlParams.create()
            .setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) )
            .setQueryParams( Map.of( "k1", List.of( "v10", "v11" ) ) )
            .setQueryParam( "k2", "v2" )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/site/request-project/request-branch/sitePath/_/com.enonic.app.myapp:myapi?k1=v10&k1=v11&k2=v2", url );
    }

    @Test
    void testSiteRequestVhostRewrite()
    {
        final ContentPath contentPath = ContentPath.from( "sitePath" );

        portalRequest.setBaseUri( "/site" );
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setContentPath( contentPath );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( contentPath );
        portalRequest.setSite( site );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/site/request-project/request-branch" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final ApiUrlParams params = ApiUrlParams.create()
            .setType( UrlTypeConstants.ABSOLUTE )
            .setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) )
            .setQueryParams( Map.of( "k1", List.of( "v10", "v11" ) ) )
            .setQueryParam( "k2", "v2" )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "http://localhost/source/sitePath/_/com.enonic.app.myapp:myapi?k1=v10&k1=v11&k2=v2", url );
    }

    @Test
    void testSiteRequestIgnoreContext()
    {
        final ContentPath contentPath = ContentPath.from( "sitePath" );

        portalRequest.setBaseUri( "/site" );
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setContentPath( contentPath );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( contentPath );
        portalRequest.setSite( site );

        final ApiUrlParams params = ApiUrlParams.create()
            .setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) )
            .setQueryParams( Map.of( "k1", List.of( "v10", "v11" ) ) )
            .setQueryParam( "k2", "v2" )
            .build();

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.apiUrl( params ) );

        assertEquals( "/site/request-project/request-branch/sitePath/_/com.enonic.app.myapp:myapi?k1=v10&k1=v11&k2=v2", url );
    }

    @Test
    void testSiteRequestWithBaseUrl()
    {
        final ContentPath contentPath = ContentPath.from( "sitePath" );

        portalRequest.setBaseUri( "/site" );
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setContentPath( contentPath );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( contentPath );
        portalRequest.setSite( site );

        final ApiUrlParams params = ApiUrlParams.create()
            .setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) )
            .setBaseUrl( "baseUrl" )
            .setQueryParams( Map.of( "k1", List.of( "v10", "v11" ) ) )
            .setQueryParam( "k2", "v2" )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "baseUrl/_/com.enonic.app.myapp:myapi?k1=v10&k1=v11&k2=v2", url );
    }

    @Test
    void testNoSiteRequestWithEmptyBaseUri()
    {
        portalRequest.setBaseUri( "" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/path/sub-path" );

        final ApiUrlParams params = ApiUrlParams.create().setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/api/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testNoSiteRequestWithEmptyBaseUriAndWithBaseUrl()
    {
        portalRequest.setBaseUri( "" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/path/sub-path" );

        final ApiUrlParams params =
            ApiUrlParams.create().setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) ).setBaseUrl( "baseUrl" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "baseUrl/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testNoSiteRequestWithBaseUriEqualAdmin()
    {
        portalRequest.setBaseUri( "/admin" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/admin" );

        final ApiUrlParams params = ApiUrlParams.create().setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/admin/com.enonic.xp.app.main/home/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testNoSiteRequestWithBaseUriEqualAdminWithBaseUrl()
    {
        portalRequest.setBaseUri( "/admin" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/admin" );

        final ApiUrlParams params =
            ApiUrlParams.create().setDescriptorKey( DescriptorKey.from( "com.enonic.app.myapp:myapi" ) ).setBaseUrl( "baseUrl" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "baseUrl/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testNoSiteRequestWithBaseUriOnApi()
    {
        portalRequest.setBaseUri( "/api/app1:api1" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/app1:api1" );

        final ApiUrlParams params = ApiUrlParams.create().setDescriptorKey( DescriptorKey.from( "app2:api2" ) ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/api/app2:api2", url );
    }

    @Test
    void testNoSiteRequestWithBaseUriOnApiWithBaseUrl()
    {
        portalRequest.setBaseUri( "/api/app1:api1" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/app1:api1" );

        final ApiUrlParams params =
            ApiUrlParams.create().setDescriptorKey( DescriptorKey.from( "app2:api2" ) ).setBaseUrl( "baseUrl" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "baseUrl/_/app2:api2", url );
    }

    @Test
    void testNoSiteRequestWithBaseUriOnApiVhostRewriteOutOfScope()
    {
        portalRequest.setBaseUri( "/api/app:api" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/app:api" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/webapp/app" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        final ApiUrlParams params = ApiUrlParams.create().setDescriptorKey( DescriptorKey.from( "app:api" ) ).build();

        final String url = this.service.apiUrl( params );
        assertThat( url ).startsWith( "/_/error/400?message=Out+of+scope." );
    }

    @Test
    void testNoSiteRequestWithBaseUriOnApiWebapp()
    {
        portalRequest.setBaseUri( "/webapp/app" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/webapp/app" );

        final ApiUrlParams params = ApiUrlParams.create().setDescriptorKey( DescriptorKey.from( "app:api" ) ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/webapp/app/_/app:api", url );
    }

    @Test
    void testNoSiteRequestWithBaseUriOnApiWebappVhostRewrite()
    {
        portalRequest.setBaseUri( "/webapp/app" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/webapp/app" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/webapp/app" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final ApiUrlParams params =
            ApiUrlParams.create().setType( UrlTypeConstants.ABSOLUTE ).setDescriptorKey( DescriptorKey.from( "app:api" ) ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "http://localhost/source/_/app:api", url );
    }

    @Test
    void testNoSiteRequestWithBaseUriOnApiWebappWithBaseUrlIgnoreVhostRewrite()
    {
        portalRequest.setBaseUri( "/webapp/app" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/webapp/app" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/webapp/app" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final ApiUrlParams params =
            ApiUrlParams.create().setDescriptorKey( DescriptorKey.from( "app:api" ) ).setBaseUrl( "baseUrl" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "baseUrl/_/app:api", url );
    }
}
