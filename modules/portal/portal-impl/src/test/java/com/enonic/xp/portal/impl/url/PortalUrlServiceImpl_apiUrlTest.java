package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.site.Site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        when( portalRequest.getRawRequest().getRequestURI() ).thenReturn( "/site/project/draft/sitePath" );

        final ApiUrlParams params = new ApiUrlParams();
        params.portalRequest( this.portalRequest );
        params.application( "com.enonic.app.myapp" );
        params.api( "myapi" );
        params.param( "k1", "v1" );
        params.param( "k2", "v2" );

        final String url = this.service.apiUrl( params );
        assertEquals( "/site/project/draft/sitePath/_/com.enonic.app.myapp/myapi?k1=v1&k2=v2", url );
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
        assertEquals( "/admin/myapplication/toolname/_/com.enonic.app.myapp/myapi", url );
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
        assertEquals( "/admin/myapplication/toolname/_/com.enonic.app.myapp/myapi", url );
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
        assertEquals( "/admin/site/inline/project/draft/sitePath/_/com.enonic.app.myapp/myapi?k1=v1&k2=v2", url );
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

        final String url = this.service.apiUrl( params );
        assertEquals( "/api/com.enonic.app.myapp/myapi?k1=v1&k2=v2", url );
    }

    @Test
    void testCreateUrlWithoutPortalRequestUnnamedAPI()
    {
        final ApiUrlParams params = new ApiUrlParams();
        params.portalRequest( null );
        params.application( "com.enonic.app.myapp" );
        params.param( "k1", "v1" );
        params.param( "k2", "v2" );

        final String url = this.service.apiUrl( params );
        assertEquals( "/api/com.enonic.app.myapp?k1=v1&k2=v2", url );
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
        assertEquals( "/admin/com.enonic.xp.app.main/home/_/com.enonic.app.myapp/myapi", url );
    }

    @Test
    void testCreateUrlWebapp()
    {
        when( portalRequest.getRawRequest().getRequestURI() ).thenReturn( "/webapp/com.enonic.app.mywebapp" );

        final ApiUrlParams params = new ApiUrlParams();
        params.portalRequest( this.portalRequest );
        params.application( "com.enonic.app.myapp" );
        params.api( "myapi" );

        final String url = this.service.apiUrl( params );
        assertEquals( "/webapp/com.enonic.app.mywebapp/_/com.enonic.app.myapp/myapi", url );
    }

    @Test
    void testCreateUrlApi()
    {
        when( portalRequest.getRawRequest().getRequestURI() ).thenReturn( "/api/myapp1/api1" );

        final ApiUrlParams params = new ApiUrlParams();
        params.portalRequest( this.portalRequest );
        params.application( "myapp2" );
        params.api( "api2" );

        final String url = this.service.apiUrl( params );
        assertEquals( "/api/myapp2/api2", url );
    }

    @Test
    void testCreateUrlInvalid()
    {
        IllegalArgumentException ex = assertThrows( IllegalArgumentException.class, () -> {
            final ApiUrlParams params = new ApiUrlParams();
            this.service.apiUrl( params );
        } );
        assertEquals( "\"application\" is required", ex.getMessage() );

        ex = assertThrows( IllegalArgumentException.class, () -> {
            final ApiUrlParams params = new ApiUrlParams();
            this.service.apiUrl( params );
        } );
        assertEquals( "\"application\" is required", ex.getMessage() );
    }
}
