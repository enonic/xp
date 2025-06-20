package com.enonic.xp.portal;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.web.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PortalRequestTest
{
    @Test
    public void setMethod()
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getMethod() );

        request.setMethod( HttpMethod.GET );
        assertEquals( HttpMethod.GET, request.getMethod() );
    }

    @Test
    public void setScheme()
    {
        final PortalRequest request = new PortalRequest();
        assertEquals( null, request.getScheme() );

        request.setScheme( "http" );
        assertEquals( "http", request.getScheme() );
    }

    @Test
    public void setHost()
    {
        final PortalRequest request = new PortalRequest();
        assertEquals( null, request.getHost() );

        request.setHost( "localhost" );
        assertEquals( "localhost", request.getHost() );
    }

    @Test
    public void setPort()
    {
        final PortalRequest request = new PortalRequest();

        request.setPort( 8080 );
        assertEquals( 8080, request.getPort() );
    }

    @Test
    public void setPath()
    {
        final PortalRequest request = new PortalRequest();
        assertEquals( null, request.getPath() );

        request.setPath( "/root" );
        assertEquals( "/root", request.getPath() );
    }

    @Test
    public void setRawPath()
    {
        final PortalRequest request = new PortalRequest();
        assertEquals( null, request.getRawPath() );

        request.setRawPath( "/root" );
        assertEquals( "/root", request.getRawPath() );
    }

    @Test
    public void setUrl()
    {
        final PortalRequest request = new PortalRequest();
        assertEquals( null, request.getUrl() );

        request.setUrl( "http.//localhost:8080/root?param=value" );
        assertEquals( "http.//localhost:8080/root?param=value", request.getUrl() );
    }

    @Test
    public void setMode()
    {
        final PortalRequest request = new PortalRequest();
        assertEquals( RenderMode.LIVE, request.getMode() );

        request.setMode( RenderMode.EDIT );
        assertEquals( RenderMode.EDIT, request.getMode() );
    }

    @Test
    public void setBranch()
        throws Exception
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getBranch() );

        request.setBranch( Branch.from( "another" ) );
        assertEquals( Branch.from( "another" ), request.getBranch() );
    }

    @Test
    public void addParam()
    {
        final PortalRequest request = new PortalRequest();
        assertNotNull( request.getParams() );
        assertEquals( 0, request.getParams().size() );

        request.getParams().put( "name", "value" );
        assertEquals( 1, request.getParams().size() );
    }

    @Test
    public void setRemoteAddress()
        throws Exception
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getRemoteAddress() );

        request.setRemoteAddress( "10.0.0.1" );
        assertEquals( "10.0.0.1", request.getRemoteAddress() );
    }
}
