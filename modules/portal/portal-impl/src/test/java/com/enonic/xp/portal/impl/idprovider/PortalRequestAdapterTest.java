package com.enonic.xp.portal.impl.idprovider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class PortalRequestAdapterTest
{

    private PortalRequestAdapter portalRequestAdapter;

    @BeforeEach
    void setUp()
    {
        portalRequestAdapter = new PortalRequestAdapter();
    }

    @Test
    void adaptTest()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/site/test/draft" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getMethod() ).isEqualTo( HttpMethod.GET );
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/site" );
    }

    @Test
    void adaptSiteLogin()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/site/test/draft/_/idprovider/system/login" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/site" );
    }

    @Test
    void adaptSite_incomplete()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/site/test/_/idprovider/system/login" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isNull();
    }

    @Test
    void adaptAdminSiteTest()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/admin/site/admin/test/draft" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin/site/admin" );
        assertThat( adaptedRequest.getMode() ).isEqualTo( RenderMode.ADMIN );
    }

    @Test
    void adaptAdminSite_incomplete()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/admin/site/admin/test" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isNull();
    }

    @Test
    void adaptAdminToolUriTest()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/admin" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin" );
        assertThat( adaptedRequest.getMode() ).isNull();
    }

    @Test
    void adaptAdminToolIdProviderUriTest()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/admin/_/idprovider/system/login" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin" );
        assertThat( adaptedRequest.getMode() ).isNull();
    }

    @Test
    void adaptAdminTool_incomplete()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/admin/a/_/idprovider/system/login" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin" );
        assertThat( adaptedRequest.getMode() ).isNull();
    }

    @Test
    void adaptAdminToolUriWithDescriptorTest()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/admin/app/tool" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/admin/app/tool" );
        assertThat( adaptedRequest.getMode() ).isNull();
    }

    @Test
    void adaptWebAppUriTest()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/webapp/app/anything" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/webapp/app" );
    }

    @Test
    void adaptWebApp_incomplete()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/webapp/" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isNull();
    }

    @Test
    void adaptNonSiteTest()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/test" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isNull();
    }

    @Test
    void adaptSlashApiTest()
    {
        // use case 1
        final WebRequest webRequest1 = new WebRequest();
        webRequest1.setRawPath( "/api/app:api" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest1 );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/api/app:api" );

        // use case 2
        final WebRequest webRequest2 = new WebRequest();
        webRequest2.setRawPath( "/api/app:api" );

        adaptedRequest = portalRequestAdapter.adapt( webRequest2 );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/api/app:api" );

        // use case 3
        final WebRequest webRequest3 = new WebRequest();
        webRequest3.setRawPath( "/api/app:api/" );

        adaptedRequest = portalRequestAdapter.adapt( webRequest3 );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isEqualTo( "/api/app:api" );
    }

    @Test
    void adaptApi_incomplete()
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setRawPath( "/api/" );

        PortalRequest adaptedRequest = portalRequestAdapter.adapt( webRequest );

        assertThat( adaptedRequest ).isNotNull();
        assertThat( adaptedRequest.getBaseUri() ).isNull();
    }
}
