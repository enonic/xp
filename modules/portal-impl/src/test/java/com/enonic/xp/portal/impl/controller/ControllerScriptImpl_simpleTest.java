package com.enonic.xp.portal.impl.controller;

import org.junit.Test;

import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class ControllerScriptImpl_simpleTest
    extends AbstractControllerTest
{
    @Test
    public void testExecute()
    {
        this.portalRequest.setMethod( "GET" );
        execute( "myapplication:/controller/simple.js" );
        assertEquals( HttpStatus.OK.value(), this.portalResponse.getStatus() );
    }


    @Test
    public void testMethodNotSupported()
    {
        this.portalRequest.setMethod( "POST" );
        execute( "myapplication:/controller/simple.js" );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED.value(), this.portalResponse.getStatus() );
    }

}
