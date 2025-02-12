package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpMethod;

class ControllerScriptImpl_jsonTest
    extends AbstractControllerTest
{
    @Test
    void testArray()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/array.js" );
        assertBodyJson( "array.json" );
    }

    @Test
    void testMap()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/controller/map.js" );
        assertBodyJson( "map.json" );
    }
}
