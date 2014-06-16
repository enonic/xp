package com.enonic.wem.portal.underscore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.collect.Multimap;
import com.sun.jersey.api.client.ClientResponse;

import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.base.ModuleBaseHandlerTest;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsHttpRequest;

import static org.junit.Assert.*;

public class ServiceHandlerTest
    extends ModuleBaseHandlerTest<ServiceHandler>
{
    private JsController jsController;

    @Override
    protected ServiceHandler createResource()
    {
        return new ServiceHandler();
    }

    @Before
    public void setup()
        throws Exception
    {
        super.setup();

        this.jsController = Mockito.mock( JsController.class );
        this.resource.controllerFactory = Mockito.mock( JsControllerFactory.class );
        Mockito.when( this.resource.controllerFactory.newController() ).thenReturn( this.jsController );

        mockCurrentContextHttpRequest();
    }

    @Test
    public void executeScript()
        throws Exception
    {
        final ClientResponse response = executeGet( "/live/path/to/content/_/service/demo-1.0.0/test?a=b" );
        assertEquals( 200, response.getStatus() );

        final ArgumentCaptor<JsContext> jsContext = ArgumentCaptor.forClass( JsContext.class );
        Mockito.verify( this.jsController ).context( jsContext.capture() );
        Mockito.verify( this.jsController ).execute();

        final JsHttpRequest jsHttpRequest = jsContext.getValue().getRequest();
        assertNotNull( jsHttpRequest );
        assertEquals( "GET", jsHttpRequest.getMethod() );
        assertEquals( RenderingMode.LIVE, jsHttpRequest.getMode() );

        final Multimap<String, String> params = jsHttpRequest.getParams();
        assertNotNull( params );
        assertEquals( "b", params.get( "a" ).iterator().next() );
    }

    @Test(expected = ModuleNotFoundException.class)
    public void executeScript_moduleNotFound()
        throws Exception
    {
        executeGet( "/live/path/to/content/_/service/demo/test" );
    }
}
