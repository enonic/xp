package com.enonic.wem.portal.internal.underscore;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.RenderingMode;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.internal.base.ModuleBaseResourceTest;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.controller.JsController;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;

import static org.junit.Assert.*;

public class ServiceResourceTest
    extends ModuleBaseResourceTest
{
    private JsController jsController;

    @Override
    protected void configure()
        throws Exception
    {
        configureModuleService();

        final ServiceResourceProvider provider = new ServiceResourceProvider();
        provider.setModuleService( this.moduleService );

        final JsControllerFactory controllerFactory = Mockito.mock( JsControllerFactory.class );
        provider.setControllerFactory( controllerFactory );

        this.jsController = Mockito.mock( JsController.class );
        Mockito.when( controllerFactory.newController( Mockito.anyObject() ) ).thenReturn( this.jsController );

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

        final ArgumentCaptor<JsContext> jsContext = ArgumentCaptor.forClass( JsContext.class );
        Mockito.verify( this.jsController ).execute( jsContext.capture() );

        final PortalRequest jsHttpRequest = jsContext.getValue().getRequest();
        assertNotNull( jsHttpRequest );
        assertEquals( "GET", jsHttpRequest.getMethod() );
        assertEquals( RenderingMode.LIVE, jsHttpRequest.getMode() );

        final Multimap<String, String> params = jsHttpRequest.getParams();
        assertNotNull( params );
        assertEquals( "b", params.get( "a" ).iterator().next() );
    }
}
