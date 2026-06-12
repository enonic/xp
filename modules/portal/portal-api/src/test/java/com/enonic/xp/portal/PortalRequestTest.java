package com.enonic.xp.portal;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.csp.ContentSecurityPolicy;
import com.enonic.xp.web.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PortalRequestTest
{
    @Test
    void setMethod()
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getMethod() );

        request.setMethod( HttpMethod.GET );
        assertEquals( HttpMethod.GET, request.getMethod() );
    }

    @Test
    void setScheme()
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getScheme() );

        request.setScheme( "http" );
        assertEquals( "http", request.getScheme() );
    }

    @Test
    void setHost()
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getHost() );

        request.setHost( "localhost" );
        assertEquals( "localhost", request.getHost() );
    }

    @Test
    void setPort()
    {
        final PortalRequest request = new PortalRequest();

        request.setPort( 8080 );
        assertEquals( 8080, request.getPort() );
    }

    @Test
    void setPath()
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getPath() );

        request.setPath( "/root" );
        assertEquals( "/root", request.getPath() );
    }

    @Test
    void setRawPath()
    {
        final PortalRequest request = new PortalRequest();
        assertEquals( "/", request.getRawPath() );

        request.setRawPath( "/root" );
        assertEquals( "/root", request.getRawPath() );
    }

    @Test
    void setUrl()
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getUrl() );

        request.setUrl( "http.//localhost:8080/root?param=value" );
        assertEquals( "http.//localhost:8080/root?param=value", request.getUrl() );
    }

    @Test
    void setMode()
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getMode() );

        request.setMode( RenderMode.EDIT );
        assertEquals( RenderMode.EDIT, request.getMode() );
    }

    @Test
    void setBranch()
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getBranch() );

        request.setBranch( Branch.from( "another" ) );
        assertEquals( Branch.from( "another" ), request.getBranch() );
    }

    @Test
    void addParam()
    {
        final PortalRequest request = new PortalRequest();
        assertNotNull( request.getParams() );
        assertEquals( 0, request.getParams().size() );

        request.getParams().put( "name", "value" );
        assertEquals( 1, request.getParams().size() );
    }

    @Test
    void setRemoteAddress()
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getRemoteAddress() );

        request.setRemoteAddress( "10.0.0.1" );
        assertEquals( "10.0.0.1", request.getRemoteAddress() );
    }

    @Test
    void getContentSecurityPolicy_same_instance_per_request()
    {
        final PortalRequest request = new PortalRequest();
        final ContentSecurityPolicy first = request.getContentSecurityPolicy();
        assertNotNull( first );
        assertThat( request.getContentSecurityPolicy() ).isSameAs( first );
    }

    @Test
    void getContentSecurityPolicy_shared_when_request_is_wrapped()
    {
        final WebRequest webRequest = new WebRequest();
        final PortalRequest portalRequest = new PortalRequest( webRequest );
        assertThat( portalRequest.getContentSecurityPolicy() ).isSameAs( webRequest.getContentSecurityPolicy() );
    }

    @Test
    void getContentSecurityPolicy_distinct_instances_per_request()
    {
        final ContentSecurityPolicy first = new PortalRequest().getContentSecurityPolicy();
        final ContentSecurityPolicy second = new PortalRequest().getContentSecurityPolicy();
        assertThat( first ).isNotSameAs( second );
    }
}
