package com.enonic.xp.portal.impl.idprovider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.web.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PortalRequestAdapterTest
{

    private PortalRequestAdapter portalRequestAdapter;

    private HttpServletRequest mockHttpServletRequest;

    @BeforeEach
    public void setUp()
    {
        portalRequestAdapter = new PortalRequestAdapter();
        mockHttpServletRequest = Mockito.mock( HttpServletRequest.class );
    }

    @Test
    public void adaptTest()
    {
        when( mockHttpServletRequest.getMethod() ).thenReturn( "GET" );
        when( mockHttpServletRequest.getContentType() ).thenReturn( "text/html" );
        when( mockHttpServletRequest.getScheme() ).thenReturn( "http" );
        when( mockHttpServletRequest.getServerName() ).thenReturn( "localhost" );
        when( mockHttpServletRequest.getRemoteAddr() ).thenReturn( "127.0.0.1" );
        when( mockHttpServletRequest.getServerPort() ).thenReturn( 8080 );
        when( mockHttpServletRequest.getPathInfo() ).thenReturn( "/test/draft" );
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
        assertThat( adaptedRequest.getRawPath() ).isEqualTo( "/test/draft" );
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/site" );
        assertThat( adaptedRequest.getParams().get( "param1" ) ).containsExactly( "value1", "value2" );
        assertThat( adaptedRequest.getCookies() ).containsAllEntriesOf( Map.of( "cookie1", "value1", "cookie2", "value2" ) );
        assertThat( adaptedRequest.getHeaders() ).containsAllEntriesOf( Map.of("header1", "value1", "header2", "value2") );
    }

    @Test
    public void adaptSiteLogin()
    {
        when( mockHttpServletRequest.getRequestURI() ).thenReturn( "/site/test/draft/_/idprovider/system/login" );
        when( mockHttpServletRequest.getMethod() ).thenReturn( "GET" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/site" );
    }

    @Test
    public void adaptAdminUriTest()
    {
        when( mockHttpServletRequest.getRequestURI() ).thenReturn( "/admin/site/admin/test/draft" );
        when( mockHttpServletRequest.getMethod() ).thenReturn( "GET" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin/site/admin" );
    }

    @Test
    public void adaptAdminToolUriTest()
    {
        when( mockHttpServletRequest.getRequestURI() ).thenReturn( "/admin/tool" );
        when( mockHttpServletRequest.getMethod() ).thenReturn( "GET" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin/tool" );
    }

    @Test
    public void adaptAdminToolIdProviderUriTest()
    {
        when( mockHttpServletRequest.getRequestURI() ).thenReturn( "/admin/tool/_/idprovider/system/login" );
        when( mockHttpServletRequest.getMethod() ).thenReturn( "GET" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin/tool" );
    }

    @Test
    public void adaptAdminToolUriWithDescriptorTest()
    {
        when( mockHttpServletRequest.getRequestURI() ).thenReturn( "/admin/tool/app/tool" );
        when( mockHttpServletRequest.getMethod() ).thenReturn( "GET" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin/tool/app/tool" );
    }

    @Test
    public void adaptAnyAdminUriWithDescriptorTest()
    {
        when( mockHttpServletRequest.getRequestURI() ).thenReturn( "/admin/path" );
        when( mockHttpServletRequest.getMethod() ).thenReturn( "GET" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin/tool" );
    }

    @Test
    public void adaptWebAppUriTest()
    {
        when( mockHttpServletRequest.getRequestURI() ).thenReturn( "/webapp/app/anything" );
        when( mockHttpServletRequest.getMethod() ).thenReturn( "GET" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/webapp/app" );
    }

    @Test
    public void adaptNonSiteTest()
    {
        when( mockHttpServletRequest.getRequestURI() ).thenReturn( "/test" );
        when( mockHttpServletRequest.getMethod() ).thenReturn( "GET" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( mockHttpServletRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "" );
    }
}
