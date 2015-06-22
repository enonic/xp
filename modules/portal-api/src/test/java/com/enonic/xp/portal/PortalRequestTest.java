package com.enonic.xp.portal;

import org.junit.Test;

import com.enonic.xp.branch.Branch;

import static org.junit.Assert.*;

public class PortalRequestTest
{
    @Test
    public void setMethod()
    {
        final PortalRequest request = new PortalRequest();
        assertNull( request.getMethod() );

        request.setMethod( "GET" );
        assertEquals( "GET", request.getMethod() );
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
        assertEquals( PortalRequest.DEFAULT_BRANCH, request.getBranch() );

        request.setBranch( Branch.from( "another" ) );
        assertEquals( Branch.from( "another" ), request.getBranch() );
    }

    @Test
    public void setBaseUrl()
    {
        final PortalRequest request = new PortalRequest();
        assertEquals( "", request.getBaseUrl() );

        request.setBaseUrl( "baseUrl" );
        assertEquals( "baseUrl", request.getBaseUrl() );
    }


    @Test
    public void setServerUrl()
    {
        final PortalRequest request = new PortalRequest();
        assertEquals( "", request.getServerUrl() );

        request.setServerUrl( "serverUrl" );
        assertEquals( "serverUrl", request.getServerUrl() );
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
}
