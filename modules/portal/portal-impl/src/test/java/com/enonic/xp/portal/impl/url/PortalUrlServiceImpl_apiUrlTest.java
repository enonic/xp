package com.enonic.xp.portal.impl.url;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.style.StyleDescriptorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_apiUrlTest
{

    private ContentService contentService;

    private ProjectService projectService;

    private PortalUrlService service;

    private PortalRequest portalRequest;

    @BeforeEach
    public void setUp()
    {
        this.contentService = mock( ContentService.class );
        this.projectService = mock( ProjectService.class );

        UrlGeneratorParamsAdapter urlGeneratorParamsAdapter = new UrlGeneratorParamsAdapter( this.contentService, this.projectService );

        this.service = new PortalUrlServiceImpl( this.contentService, mock( ResourceService.class ), mock( MacroService.class ),
                                                 mock( StyleDescriptorService.class ), mock( RedirectChecksumService.class ),
                                                 urlGeneratorParamsAdapter );

        final HttpServletRequest req = mock( HttpServletRequest.class );

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
            .setOffline( true )
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
            .setOffline( true )
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .setBaseUrlKey( "contentId" )
            .setProjectName( "myproject" )
            .setBranch( "master" )
            .addQueryParam( "k1", "v1" )
            .addQueryParam( "k2", "v2" )
            .build();

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        SiteConfigs siteConfigs = SiteConfigs.create()
            .add( SiteConfig.create().application( ApplicationKey.from( "com.enonic.xp.site" ) ).config( config ).build() )
            .build();

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getSiteConfigs() ).thenReturn( siteConfigs );

        when( contentService.getNearestSite( eq( ContentId.from( "contentId" ) ) ) ).thenReturn( site );

        assertEquals( "https://cdn.company.com/_/com.enonic.app.myapp:myapi?k1=v1&k2=v2", this.service.apiUrl( params ) );
    }

    @Test
    void testCreateUrlOfflineWithBaseUrlKeyBaseUrlFromProject()
    {
        final ApiUrlParams params = ApiUrlParams.create()
            .setType( UrlTypeConstants.ABSOLUTE )
            .setOffline( true )
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .setBaseUrlKey( "contentId" )
            .setProjectName( "myproject" )
            .setBranch( "master" )
            .addQueryParam( "k1", "v1" )
            .addQueryParam( "k2", "v2" )
            .build();

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        SiteConfigs siteConfigs = SiteConfigs.create()
            .add( SiteConfig.create().application( ApplicationKey.from( "com.enonic.xp.site" ) ).config( config ).build() )
            .build();

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getSiteConfigs() ).thenReturn( SiteConfigs.empty() );

        when( contentService.getNearestSite( eq( ContentId.from( "contentId" ) ) ) ).thenReturn( site );

        final Project project = mock( Project.class );
        when( project.getSiteConfigs() ).thenReturn( siteConfigs );
        when( projectService.get( eq( ProjectName.from( "myproject" ) ) ) ).thenReturn( project );

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
        portalRequest.setBaseUri( "/api" );
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
    void testCreateUrlApiWithSupPathAsString()
    {
        portalRequest.setBaseUri( "/api" );
        portalRequest.setRawPath( "/api/myapp1/api1" );

        ApiUrlParams params =
            ApiUrlParams.create().setApplication( "com.enonic.app.myapp" ).setApi( "myapi" ).setPath( "bl%C3%A5%20lagune.png" ).build();

        assertEquals( "/api/com.enonic.app.myapp:myapi/bl%C3%A5%20lagune.png", this.service.apiUrl( params ) );
    }

    @Test
    void testCreateUrlWithCustomBaseUrl()
    {
        final ApiUrlGeneratorParams params = ApiUrlGeneratorParams.create()
            .setBaseUrlStrategy( () -> "myCustomBaseUrl" )
            .setApplication( "com.enonic.app.myapp" )
            .setApi( "myapi" )
            .addQueryParam( "k1", "v1" )
            .addQueryParam( "k2", "v2" )
            .build();

        final String url = this.service.apiUrl( params );
        assertEquals( "myCustomBaseUrl/com.enonic.app.myapp:myapi?k1=v1&k2=v2", url );
    }
}
