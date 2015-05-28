package com.enonic.xp.portal.impl.controller;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.portal.PortalResponse;

import static org.junit.Assert.*;

public class ControllerScriptImpl_simpleTest
    extends AbstractControllerTest
{
    @Test
    public void testExecute()
    {
        this.portalRequest.setMethod( "GET" );
        execute( "mymodule:/controller/simple.js" );
        assertEquals( PortalResponse.STATUS_OK, this.response.getStatus() );
    }

    @Test
    public void testExecutePostProcess()
    {
        this.portalRequest.setMethod( "GET" );
        this.response.setPostProcess( true );

        execute( "mymodule:/controller/simple.js" );

        assertEquals( PortalResponse.STATUS_OK, this.response.getStatus() );
        Mockito.verify( this.postProcessor ).processResponse( this.portalRequest );
    }

    @Test
    public void testMethodNotSupported()
    {
        this.portalRequest.setMethod( "POST" );
        execute( "mymodule:/controller/simple.js" );
        assertEquals( PortalResponse.STATUS_METHOD_NOT_ALLOWED, this.response.getStatus() );
    }

}
