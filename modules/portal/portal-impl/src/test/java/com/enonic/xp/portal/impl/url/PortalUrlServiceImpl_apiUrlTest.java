package com.enonic.xp.portal.impl.url;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.Site;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    void testCreateUrl()
    {
        final ContentPath contentPath = ContentPath.from( "sitePath" );

        portalRequest.setBaseUri( "/site" );
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
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

        final String url = this.service.apiUrl( params );
        assertEquals( "/site/myproject/draft/sitePath/_/com.enonic.app.myapp:myapi?k1=v10&k1=v11&k2=v2", url );
    }

    @Test
    void testCreateUrlAdminTool()
    {
        portalRequest.setBaseUri( "/admin/myapplication/toolname" );

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/admin/myapplication/toolname/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testCreateUrlAdminToolWithAppFromRequest()
    {
        portalRequest.setBaseUri( "/admin/myapplication/toolname" );

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/admin/myapplication/toolname/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testCreateUrlAdminSite()
    {
        final ContentPath contentPath = ContentPath.from( "sitePath" );
        portalRequest.setContentPath( contentPath );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( contentPath );
        when( contentService.findNearestSiteByPath( eq( contentPath ) ) ).thenReturn( site );

        portalRequest.setBaseUri( "/admin/site/inline" );
        portalRequest.setRawPath( "/admin/site/inline/project/draft/sitePath" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        portalRequest.setBranch( Branch.from( "draft" ) );

        final ApiUrlParams params = ApiUrlParams.create()
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .addQueryParam( "k1", "v1" )
            .addQueryParam( "k2", "v2" )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/admin/site/inline/myproject/draft/sitePath/_/com.enonic.app.myapp:myapi?k1=v1&k2=v2", url );
    }

    @Test
    void testCreateUrlOffline()
    {
        final ApiUrlParams params = ApiUrlParams.create()
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .setPathSegments( List.of( "språk", "kurs" ) )
            .addQueryParam( "k1", "v1" )
            .addQueryParam( "k2", "v2" )
            .build();

        assertEquals( "/api/com.enonic.app.myapp:myapi/spr%C3%A5k/kurs?k1=v1&k2=v2", this.service.apiUrl( params ) );
    }

    @Test
    void testCreateUrlOfflineWithBaseUrlKey()
    {
        final ApiUrlParams params = ApiUrlParams.create()
            .setType( UrlTypeConstants.ABSOLUTE )
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .setBaseUrl( "https://cdn.company.com" )
            .addQueryParam( "k1", "v1" )
            .addQueryParam( "k2", "v2" )
            .build();

        assertEquals( "https://cdn.company.com/_/com.enonic.app.myapp:myapi?k1=v1&k2=v2", this.service.apiUrl( params ) );
    }

    @Test
    void testCreateUrlWithoutPortalRequestWithPathAndPathSegments()
    {
        IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> {
            final ApiUrlParams params = ApiUrlParams.create()
                .setApplication( "com.enonic.app.myapp" )
                .setApi( "myapi" )
                .setPath( "språk/kurs" )
                .setPathSegments( List.of( "språk", "kurs" ) )
                .addQueryParam( "k1", "v1" )
                .addQueryParam( "k2", "v2" )
                .build();

            this.service.apiUrl( params );
        } );
        assertEquals( "Both path and pathSegments cannot be set", exception.getMessage() );
    }

    @Test
    void testCreateUrlAdminHome()
    {
        portalRequest.setBaseUri( "/admin" );

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/admin/com.enonic.xp.app.main/home/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testCreateUrlWebapp()
    {
        portalRequest.setBaseUri( "/webapp/com.enonic.app.mywebapp" );

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/webapp/com.enonic.app.mywebapp/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testCreateUrlApi()
    {
        portalRequest.setBaseUri( "/api/com.enonic.app.guillotine:graphql" );
        portalRequest.setRawPath( "/api/com.enonic.app.guillotine:graphql" );

        final ApiUrlParams params = ApiUrlParams.create()
            .setApplication( "media" )
            .setApi( "image" )
            .setPathSegments( List.of( "project", "id:hash", "scale-100-100", "blå lagune.png" ) )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/api/media:image/project/id:hash/scale-100-100/bl%C3%A5%20lagune.png", url );
    }

    @Test
    void testWithNoSiteRequestInContextWithVirtualHost()
    {
        portalRequest.setBaseUri( "/api/guillotine:graphql" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/guillotine:graphql" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/api/media:image" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final ApiUrlParams params = ApiUrlParams.create()
            .setType( UrlTypeConstants.ABSOLUTE )
            .setApplication( "media" )
            .setApi( "image" )
            .setPathSegments( List.of( "context-project:context-branch", "id:hash", "scale", "name.png" ) )
            .build();

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.apiUrl( params ) );

        assertEquals( "http://localhost/source/context-project:context-branch/id:hash/scale/name.png", url );
    }

    @Test
    void testWithSiteRequestWithVirtualHost()
    {
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/site/request-project/request-branch" );
        portalRequest.setContentPath( ContentPath.from( "/mysite" ) );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/site/request-project/request-branch/mysite" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );

        when( contentService.findNearestSiteByPath( any( ContentPath.class ) ) ).thenReturn( site );

        final ApiUrlParams params = ApiUrlParams.create()
            .setType( UrlTypeConstants.ABSOLUTE )
            .setApplication( "media" )
            .setApi( "image" )
            .setPathSegments( List.of( "context-project:context-branch", "id:hash", "scale", "name.png" ) )
            .build();

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.apiUrl( params ) );

        assertEquals( "http://localhost/source/_/media:image/context-project:context-branch/id:hash/scale/name.png", url );
    }

    @Test
    void testCreateUrlApiWithSupPathAsString()
    {
        portalRequest.setBaseUri( "/api/myapp1:api1" );
        portalRequest.setRawPath( "/api/myapp1:api1" );

        ApiUrlParams params =
            ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).setPath( "bl%C3%A5%20lagune.png" ).build();

        assertEquals( "/api/com.enonic.app.myapp:myapi/bl%C3%A5%20lagune.png", this.service.apiUrl( params ) );
    }

    @Test
    void testCreateUrlWithCustomBaseUrlJs()
    {
        final ApiUrlParams params = ApiUrlParams.create()
            .setType( UrlTypeConstants.ABSOLUTE )
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .setBaseUrl( "https://api.mycompany.com" )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "https://api.mycompany.com/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testCreateUrlWithCustomBaseUrlJsServerRelative()
    {
        final ApiUrlParams params = ApiUrlParams.create()
            .setType( UrlTypeConstants.SERVER_RELATIVE )
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .setBaseUrl( "https://api.mycompany.com" )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "https://api.mycompany.com/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testCreateUrlWithoutCustomBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final ApiUrlParams params = ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).build();

        final String url = this.service.apiUrl( params );
        assertEquals( "/api/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testCreateUrlWithCustomBaseUrl()
    {
        final ApiUrlGeneratorParams params = ApiUrlGeneratorParams.create()
            .setBaseUrl( "https://api.mycompany.com" )
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .addQueryParam( "k1", "v1" )
            .addQueryParam( "k2", "v2" )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "https://api.mycompany.com/_/com.enonic.app.myapp:myapi?k1=v1&k2=v2", url );
    }
}
