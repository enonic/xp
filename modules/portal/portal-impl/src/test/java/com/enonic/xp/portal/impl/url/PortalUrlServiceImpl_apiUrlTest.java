package com.enonic.xp.portal.impl.url;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.ApiUrlParams;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_apiUrlTest
{

    private ContentService contentService;

    private PortalUrlService service;

    private PortalRequest portalRequest;

    private HttpServletRequest req;

    @BeforeEach
    public void setUp()
    {
        this.contentService = mock( ContentService.class );

        this.service = new PortalUrlServiceImpl( this.contentService, mock( ResourceService.class ), mock( MacroService.class ),
                                                 mock( StyleDescriptorService.class ), mock( RedirectChecksumService.class ),
                                                 mock( ProjectService.class ) );

        req = mock( HttpServletRequest.class );

        portalRequest = new PortalRequest();
        portalRequest.setRawRequest( req );

        PortalRequestAccessor.set( portalRequest );
    }

    @Test
    public void destroy()
    {
        PortalRequestAccessor.remove();
    }

    @Test
    void testNoRequest()
    {
        PortalRequestAccessor.set( null );

        final ApiUrlParams params =
            ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).setPath( "path" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/api/com.enonic.app.myapp:myapi/path", url );
    }

    @Test
    void testNoRequestWithoutApp()
    {
        PortalRequestAccessor.set( null );

        final ApiUrlParams params = ApiUrlParams.create().setApi( "myapi" ).setPath( "path" ).build();

        final String url = this.service.apiUrl( params );
        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void testNoRequestWithBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final ApiUrlParams params = ApiUrlParams.create()
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
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
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setContentPath( contentPath );
        portalRequest.setApplicationKey( ApplicationKey.from( "com.enonic.app.myapp" ) );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( contentPath );
        when( contentService.findNearestSiteByPath( eq( contentPath ) ) ).thenReturn( site );

        final ApiUrlParams params = ApiUrlParams.create()
            .setApi( "myapi" )
            .addQueryParam( "k1", "v10" )
            .addQueryParam( "k1", "v11" )
            .addQueryParam( "k2", "v2" )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/site/request-project/request-branch/sitePath/_/com.enonic.app.myapp:myapi?k1=v10&k1=v11&k2=v2", url );
    }

    @Test
    void testSiteRequestVhostRewrite()
    {
        final ContentPath contentPath = ContentPath.from( "sitePath" );

        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setContentPath( contentPath );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( contentPath );
        when( contentService.findNearestSiteByPath( eq( contentPath ) ) ).thenReturn( site );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/site/request-project/request-branch" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final ApiUrlParams params = ApiUrlParams.create()
            .setType( UrlTypeConstants.ABSOLUTE )
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .addQueryParam( "k1", "v10" )
            .addQueryParam( "k1", "v11" )
            .addQueryParam( "k2", "v2" )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "http://localhost/source/sitePath/_/com.enonic.app.myapp:myapi?k1=v10&k1=v11&k2=v2", url );
    }

    @Test
    void testSiteRequestIgnoreContext()
    {
        final ContentPath contentPath = ContentPath.from( "sitePath" );

        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setContentPath( contentPath );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( contentPath );
        when( contentService.findNearestSiteByPath( eq( contentPath ) ) ).thenReturn( site );

        final ApiUrlParams params = ApiUrlParams.create()
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .addQueryParam( "k1", "v10" )
            .addQueryParam( "k1", "v11" )
            .addQueryParam( "k2", "v2" )
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
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setContentPath( contentPath );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( contentPath );
        when( contentService.findNearestSiteByPath( eq( contentPath ) ) ).thenReturn( site );

        final ApiUrlParams params = ApiUrlParams.create()
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .setBaseUrl( "baseUrl" )
            .addQueryParam( "k1", "v10" )
            .addQueryParam( "k1", "v11" )
            .addQueryParam( "k2", "v2" )
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

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).build();

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
            ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).setBaseUrl( "baseUrl" ).build();

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

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).build();

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
            ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).setBaseUrl( "baseUrl" ).build();

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

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "app2" ).setApi( "api2" ).build();

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

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "app2" ).setApi( "api2" ).setBaseUrl( "baseUrl" ).build();

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

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "app" ).setApi( "api" ).build();

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

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "app" ).setApi( "api" ).build();

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
            ApiUrlParams.create().setType( UrlTypeConstants.ABSOLUTE ).setApplication( "app" ).setApi( "api" ).build();

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

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "app" ).setApi( "api" ).setBaseUrl( "baseUrl" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "baseUrl/_/app:api", url );
    }
}
