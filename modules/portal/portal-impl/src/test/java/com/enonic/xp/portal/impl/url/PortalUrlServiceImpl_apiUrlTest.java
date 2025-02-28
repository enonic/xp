package com.enonic.xp.portal.impl.url;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.site.Site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_apiUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    void testCreateUrl()
    {
        final ContentPath contentPath = ContentPath.from( "sitePath" );
        portalRequest.setContentPath( contentPath );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( contentPath );
        when( contentService.findNearestSiteByPath( eq( contentPath ) ) ).thenReturn( site );

        final ApiUrlParams params = new ApiUrlParams();

        params.application( "com.enonic.app.myapp" );
        params.api( "myapi" );
        params.param( "k1", "v1" );
        params.param( "k2", "v2" );

        final String url = this.service.apiUrl( params );
        assertEquals( "/site/myproject/draft/sitePath/_/com.enonic.app.myapp:myapi?k1=v1&k2=v2", url );
    }

    @Test
    void testCreateUrlAdminTool()
    {
        when( portalRequest.getRawRequest().getRequestURI() ).thenReturn( "/admin/myapplication/toolname" );

        final ApiUrlParams params = new ApiUrlParams();
        params.portalRequest( this.portalRequest );
        params.application( "com.enonic.app.myapp" );
        params.api( "myapi" );

        final String url = this.service.apiUrl( params );
        assertEquals( "/admin/myapplication/toolname/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testCreateUrlAdminToolWithAppFromRequest()
    {
        when( portalRequest.getRawRequest().getRequestURI() ).thenReturn( "/admin/myapplication/toolname" );

        final ApiUrlParams params = new ApiUrlParams();
        params.portalRequest( this.portalRequest );
        params.application( "com.enonic.app.myapp" );
        params.api( "myapi" );

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

        when( portalRequest.getRawRequest().getRequestURI() ).thenReturn( "/admin/site/inline/project/draft/sitePath" );

        final ApiUrlParams params = new ApiUrlParams();
        params.portalRequest( this.portalRequest );
        params.application( "com.enonic.app.myapp" );
        params.api( "myapi" );
        params.param( "k1", "v1" );
        params.param( "k2", "v2" );

        final String url = this.service.apiUrl( params );
        assertEquals( "/admin/site/inline/project/draft/sitePath/_/com.enonic.app.myapp:myapi?k1=v1&k2=v2", url );
    }

    @Test
    void testCreateUrlWithoutPortalRequest()
    {
        final ApiUrlParams params = new ApiUrlParams();
        params.portalRequest( null );
        params.application( "com.enonic.app.myapp" );
        params.api( "myapi" );
        params.param( "k1", "v1" );
        params.param( "k2", "v2" );
        params.pathSegments( List.of( "språk", "kurs" ) );

        assertEquals( "/api/com.enonic.app.myapp:myapi/spr%C3%A5k/kurs?k1=v1&k2=v2", this.service.apiUrl( params ) );

        params.path( "språk/kurs" );
        assertEquals( "/api/com.enonic.app.myapp:myapi/språk/kurs/spr%C3%A5k/kurs?k1=v1&k2=v2", this.service.apiUrl( params ) );
    }

    @Test
    void testCreateUrlAdminHome()
    {
        when( portalRequest.getRawRequest().getRequestURI() ).thenReturn( "/admin" );

        final ApiUrlParams params = new ApiUrlParams();
        params.portalRequest( this.portalRequest );
        params.application( "com.enonic.app.myapp" );
        params.api( "myapi" );

        final String url = this.service.apiUrl( params );
        assertEquals( "/admin/com.enonic.xp.app.main/home/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testCreateUrlWebapp()
    {
        when( portalRequest.getBaseUri() ).thenReturn( "/webapp/com.enonic.app.mywebapp" );
//        when( portalRequest.getRawRequest().getRequestURI() ).thenReturn( "/webapp/com.enonic.app.mywebapp" );

        final ApiUrlParams params = new ApiUrlParams();
        params.application( "com.enonic.app.myapp" );
        params.api( "myapi" );

        final String url = this.service.apiUrl( params );
        assertEquals( "/webapp/com.enonic.app.mywebapp/_/com.enonic.app.myapp:myapi", url );
    }

    @Test
    void testCreateUrlApi()
    {
        when( portalRequest.getRawRequest().getRequestURI() ).thenReturn( "/api/myapp1:api1" );

        final ApiUrlParams params = new ApiUrlParams();
        params.portalRequest( this.portalRequest );
        params.application( "myapp2" );
        params.api( "api2" );
        params.pathSegments( List.of( "språk", "kurs" ) );

        final String url = this.service.apiUrl( params );
        assertEquals( "/api/myapp2:api2/spr%C3%A5k/kurs", url );
    }

    @Test
    void testCreateUrlApiWithSupPathAsString()
    {
        when( portalRequest.getRawRequest().getRequestURI() ).thenReturn( "/api/myapp1/api1" );

        final ApiUrlParams params = new ApiUrlParams();
        params.portalRequest( this.portalRequest );
        params.application( "myapp2" );
        params.api( "api2" );
        params.path( "/path/subPath" );

        assertEquals( "/api/myapp2:api2/path/subPath", this.service.apiUrl( params ) );

        params.path( "path/subPath" );
        assertEquals( "/api/myapp2:api2/path/subPath", this.service.apiUrl( params ) );

        params.path( "path/sub Path" );
        assertEquals( "/api/myapp2:api2/path/sub Path", this.service.apiUrl( params ) );
    }
}
