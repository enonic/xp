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
        this.context.setMethod( "GET" );
        execute( "mymodule:/controller/simple.js" );
        assertEquals( PortalResponse.STATUS_OK, this.response.getStatus() );
    }

    @Test
    public void testExecutePostProcess()
    {
        this.context.setMethod( "GET" );
        this.response.setPostProcess( true );

        execute( "mymodule:/controller/simple.js" );

        assertEquals( PortalResponse.STATUS_OK, this.response.getStatus() );
        Mockito.verify( this.postProcessor ).processResponse( this.context );
    }

    @Test
    public void testMethodNotSupported()
    {
        this.context.setMethod( "POST" );
        execute( "mymodule:/controller/simple.js" );
        assertEquals( PortalResponse.STATUS_METHOD_NOT_ALLOWED, this.response.getStatus() );
    }

}
