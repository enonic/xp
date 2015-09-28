package com.enonic.xp.portal.impl.controller;

import org.junit.Test;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class ControllerScriptImpl_simpleTest
    extends AbstractControllerTest
{
    @Test
    public void testExecute()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/simple.js" );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }


    @Test
    public void testMethodNotSupported()
    {
        this.portalRequest.setMethod( HttpMethod.POST );
        execute( "myapplication:/controller/simple.js" );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, this.portalResponse.getStatus() );
    }

}
