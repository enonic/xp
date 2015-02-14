package com.enonic.xp.portal;

import org.junit.Test;

import com.enonic.xp.core.branch.Branch;

import static org.junit.Assert.*;

public class PortalContextTest
{
    @Test
    public void setMethod()
    {
        final PortalContext request = new PortalContext();
        assertNull( request.getMethod() );

        request.setMethod( "GET" );
        assertEquals( "GET", request.getMethod() );
    }

    @Test
    public void setMode()
    {
        final PortalContext request = new PortalContext();
        assertEquals( RenderMode.LIVE, request.getMode() );

        request.setMode( RenderMode.EDIT );
        assertEquals( RenderMode.EDIT, request.getMode() );
    }

    @Test
    public void setBranch()
        throws Exception
    {
        final PortalContext request = new PortalContext();
        assertEquals( PortalContext.DEFAULT_BRANCH, request.getBranch() );

        request.setBranch( Branch.from( "another" ) );
        assertEquals( Branch.from( "another" ), request.getBranch() );
    }

    @Test
    public void addParam()
    {
        final PortalContext request = new PortalContext();
        assertNotNull( request.getParams() );
        assertEquals( 0, request.getParams().size() );

        request.getParams().put( "name", "value" );
        assertEquals( 1, request.getParams().size() );
    }
}
