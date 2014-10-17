package com.enonic.wem.portal.internal.underscore;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.RenderingMode;
import com.enonic.wem.portal.internal.base.ModuleBaseResourceTest;
import com.enonic.wem.portal.internal.controller.ControllerScript;
import com.enonic.wem.portal.internal.controller.ControllerScriptFactory;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;

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

        final ServiceResourceProvider provider = new ServiceResourceProvider();
        provider.setModuleService( this.moduleService );

        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        provider.setControllerScriptFactory( controllerScriptFactory );

        this.controllerScript = Mockito.mock( ControllerScript.class );
        Mockito.when( controllerScriptFactory.newController( Mockito.anyObject() ) ).thenReturn( this.controllerScript );

        this.resources.add( provider );
    }

    @Test
    public void executeScript()
        throws Exception
    {
        final MockHttpServletRequest request = newGetRequest( "/live/path/to/content/_/service/demo/test" );
        request.setQueryString( "a=b" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );

        final ArgumentCaptor<PortalContextImpl> jsContext = ArgumentCaptor.forClass( PortalContextImpl.class );
        Mockito.verify( this.controllerScript ).execute( jsContext.capture() );

        final PortalRequest jsHttpRequest = jsContext.getValue().getRequest();
        assertNotNull( jsHttpRequest );
        assertEquals( "GET", jsHttpRequest.getMethod() );
        assertEquals( RenderingMode.LIVE, jsHttpRequest.getMode() );

        final Multimap<String, String> params = jsHttpRequest.getParams();
        assertNotNull( params );
        assertEquals( "b", params.get( "a" ).iterator().next() );
    }
}
