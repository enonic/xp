package com.enonic.xp.portal.impl.resource.service;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.impl.controller.ControllerScript;
import com.enonic.xp.portal.impl.controller.ControllerScriptFactory;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.resource.base.ModuleBaseResourceTest;

import static org.junit.Assert.*;

public class ServiceResourceTest
    extends ModuleBaseResourceTest
{
    private ControllerScript controllerScript;

    @Override
    protected void configure()
        throws Exception
    {
        configureModuleService();

        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        this.services.setControllerScriptFactory( controllerScriptFactory );

        this.controllerScript = Mockito.mock( ControllerScript.class );
        Mockito.when( controllerScriptFactory.newController( Mockito.anyObject() ) ).thenReturn( this.controllerScript );
    }

    @Test
    public void executeScript()
        throws Exception
    {
        final MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/service/demo/test" );
        request.setQueryString( "a=b" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );

        final ArgumentCaptor<PortalContext> jsContext = ArgumentCaptor.forClass( PortalContext.class );
        Mockito.verify( this.controllerScript ).execute( jsContext.capture() );

        final PortalRequest jsHttpRequest = jsContext.getValue();
        assertNotNull( jsHttpRequest );
        assertEquals( "GET", jsHttpRequest.getMethod() );
        assertEquals( RenderMode.LIVE, jsHttpRequest.getMode() );

        final Multimap<String, String> params = jsHttpRequest.getParams();
        assertNotNull( params );
        assertEquals( "b", params.get( "a" ).iterator().next() );
    }
}
