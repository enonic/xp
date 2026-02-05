package com.enonic.xp.portal.impl.idprovider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PortalRequestAdapterTest
{

    private PortalRequestAdapter portalRequestAdapter;

    private HttpServletRequest mockHttpServletRequest;

    @BeforeEach
    void setUp()
    {
        portalRequestAdapter = new PortalRequestAdapter();
        mockHttpServletRequest = Mockito.mock( HttpServletRequest.class );
        when( mockHttpServletRequest.getMethod() ).thenReturn( "GET" );
        when( mockHttpServletRequest.getLocales() ).thenReturn( Collections.enumeration( Collections.singleton( Locale.US ) ) );
    }

    @Test
    void adaptTest()
    {
        when( mockHttpServletRequest.getLocales() ).thenReturn( Collections.enumeration( Collections.singleton( Locale.US ) ) );
        when( mockHttpServletRequest.getContentType() ).thenReturn( "text/html" );
        when( mockHttpServletRequest.getScheme() ).thenReturn( "http" );
        when( mockHttpServletRequest.getServerName() ).thenReturn( "localhost" );
        when( mockHttpServletRequest.getRemoteAddr() ).thenReturn( "127.0.0.1" );
        when( mockHttpServletRequest.getServerPort() ).thenReturn( 8080 );
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/site/test/draft" );
        when( mockHttpServletRequest.getRequestURI() ).thenReturn( "/site/test/draft" );
        when( mockHttpServletRequest.getHeaderNames() ).thenReturn( Collections.emptyEnumeration() );

        when( mockHttpServletRequest.getParameterMap() ).thenReturn(
            Collections.singletonMap( "param1", new String[]{"value1", "value2"} ) );

        Cookie[] cookies = new Cookie[]{new Cookie( "cookie1", "value1" ), new Cookie( "cookie2", "value2" )};
        when( mockHttpServletRequest.getCookies() ).thenReturn( cookies );

        when( mockHttpServletRequest.getHeaderNames() ).thenReturn( Collections.enumeration( Arrays.asList( "header1", "header2" ) ) );
        when( mockHttpServletRequest.getHeader( "header1" ) ).thenReturn( "value1" );
        when( mockHttpServletRequest.getHeader( "header2" ) ).thenReturn( "value2" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getMethod() ).isEqualTo( HttpMethod.GET );
        assertThat( adaptedRequest.getContentType() ).isEqualTo( "text/html" );
        assertThat( adaptedRequest.getScheme() ).isEqualTo( "http" );
        assertThat( adaptedRequest.getHost() ).isEqualTo( "localhost" );
        assertThat( adaptedRequest.getRemoteAddress() ).isEqualTo( "127.0.0.1" );
        assertThat( adaptedRequest.getPort() ).isEqualTo( 8080 );
        assertThat( adaptedRequest.getRawPath() ).isEqualTo( "/site/test/draft" );
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/site" );
        assertThat( adaptedRequest.getParams().get( "param1" ) ).containsExactly( "value1", "value2" );
        assertThat( adaptedRequest.getCookies() ).containsAllEntriesOf( Map.of( "cookie1", "value1", "cookie2", "value2" ) );
        assertThat( adaptedRequest.getHeaders() ).containsAllEntriesOf( Map.of( "header1", "value1", "header2", "value2" ) );
    }

    @Test
    void adaptSiteLogin()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/site/test/draft/_/idprovider/system/login" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/site" );
    }

    @Test
    void adaptSite_incomplete()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/site/test/_/idprovider/system/login" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isNull();
    }

    @Test
    void adaptAdminSiteTest()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/admin/site/admin/test/draft" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin/site/admin" );
        assertThat( adaptedRequest.getMode() ).isEqualTo( RenderMode.ADMIN );
    }

    @Test
    void adaptAdminSite_incomplete()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/admin/site/admin/test" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isNull();
    }

    @Test
    void adaptAdminToolUriTest()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/admin" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin" );
        assertThat( adaptedRequest.getMode() ).isNull();
    }

    @Test
    void adaptAdminToolIdProviderUriTest()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/admin/_/idprovider/system/login" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin" );
        assertThat( adaptedRequest.getMode() ).isNull();
    }

    @Test
    void adaptAdminTool_incomplete()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/admin/a/_/idprovider/system/login" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin" );
        assertThat( adaptedRequest.getMode() ).isNull();
    }

    @Test
    void adaptAdminToolUriWithDescriptorTest()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/admin/app/tool" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin/app/tool" );
        assertThat( adaptedRequest.getMode() ).isNull();
    }

    @Test
    void adaptWebAppUriTest()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/webapp/app/anything" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/webapp/app" );
    }

    @Test
    void adaptWebApp_incomplete()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/webapp/" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isNull();
    }

    @Test
    void adaptNonSiteTest()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/test" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isNull();
    }

    @Test
    void adaptSlashApiTest()
    {
        // use case 1
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/api/app:api" );
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/api/app:api" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/api/app:api" );

        // use case 2
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/api/app:api" );

        adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/api/app:api" );

        // use case 3
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/api/app:api/" );

        adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/api/app:api" );
    }

    @Test
    void adaptApi_incomplete()
    {
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/api/" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isNull();
    }
}
