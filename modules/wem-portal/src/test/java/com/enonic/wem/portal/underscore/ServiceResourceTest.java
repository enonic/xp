package com.enonic.wem.portal.underscore;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsHttpRequest;

import static org.junit.Assert.*;

public class ServiceResourceTest
    extends UnderscoreResourceTest<ServiceResource>
{
    private JsController jsController;

    @Override
    protected void configure()
        throws Exception
    {
        this.resource = new ServiceResource();
        super.configure();

        this.jsController = Mockito.mock( JsController.class );
        this.resource.controllerFactory = Mockito.mock( JsControllerFactory.class );
        Mockito.when( this.resource.controllerFactory.newController() ).thenReturn( this.jsController );

        mockCurrentContextHttpRequest();
    }

    @Test
    public void executeScript()
        throws Exception
    {
        final Request request = new Request( Method.GET, "/live/path/to/content/_/service/demo-1.0.0/test?a=b" );
        executeRequest( request );

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

    @Test
    public void executeScript_moduleNotFound()
        throws Exception
    {
        final Request request = new Request( Method.GET, "/live/path/to/content/_/service/demo/test" );
        final Response response = executeRequest( request );

        assertEquals( 404, response.getStatus().getCode() );
        assertEquals( "Module [demo] not found", response.getStatus().getDescription() );
    }
}
