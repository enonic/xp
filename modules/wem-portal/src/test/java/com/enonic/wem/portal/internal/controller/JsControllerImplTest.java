package com.enonic.wem.portal.internal.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.portal.PortalResponse;
import com.enonic.wem.portal.internal.postprocess.PostProcessor;
import com.enonic.wem.script.internal.ScriptEnvironment;
import com.enonic.wem.script.internal.ScriptServiceImpl;

import static org.junit.Assert.*;

public class JsControllerImplTest
{
    private JsControllerImpl controller;

    private PostProcessor postProcessor;

    private JsContext context;

    private JsHttpRequest request;

    private PortalResponse response;

    @Before
    public void setup()
        throws Exception
    {
        ResourceUrlTestHelper.mockModuleScheme().modulesClassLoader( getClass().getClassLoader() );

        this.context = new JsContext();
        this.response = this.context.getResponse();

        final ScriptEnvironment environment = Mockito.mock( ScriptEnvironment.class );
        this.controller = new JsControllerImpl( new ScriptServiceImpl( environment ) );

        this.postProcessor = Mockito.mock( PostProcessor.class );
        this.controller.postProcessor( this.postProcessor );
        this.controller.context( this.context );

        final ResourceKey scriptDir = ResourceKey.from( "mymodule-1.0.0:/service/test" );
        this.controller.scriptDir( scriptDir );

        this.request = new JsHttpRequest();
        this.context.setRequest( this.request );
    }

    @Test
    public void testExecute()
    {
        this.request.setMethod( "GET" );
        this.controller.execute();

        assertEquals( JsHttpResponse.STATUS_OK, this.response.getStatus() );
    }

    @Test
    public void testExecutePostProcess()
    {
        this.request.setMethod( "GET" );
        this.response.setPostProcess( true );
        this.controller.execute();

        assertEquals( JsHttpResponse.STATUS_OK, this.response.getStatus() );
        Mockito.verify( this.postProcessor ).processResponse( this.context );
    }

    @Test
    public void testMethodNotSupported()
    {
        this.request.setMethod( "POST" );
        this.controller.execute();
        assertEquals( JsHttpResponse.STATUS_METHOD_NOT_ALLOWED, this.response.getStatus() );
    }
}
