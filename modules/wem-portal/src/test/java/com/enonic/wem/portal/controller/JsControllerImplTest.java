package com.enonic.wem.portal.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.portal.postprocess.PostProcessor;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.loader.ScriptSource;
import com.enonic.wem.portal.script.runner.ScriptRunner;

import static org.junit.Assert.*;

public class JsControllerImplTest
{
    private ScriptRunner scriptRunner;

    private JsControllerImpl controller;

    private PostProcessor postProcessor;

    private JsContext context;

    private JsHttpRequest request;

    private ResourceKey scriptDir;

    private ScriptLoader scriptLoader;

    private JsHttpResponse response;

    @Before
    public void setup()
    {
        this.context = new JsContext();
        this.response = this.context.getResponse();

        this.scriptRunner = Mockito.mock( ScriptRunner.class );
        this.controller = new JsControllerImpl( this.scriptRunner );

        this.postProcessor = Mockito.mock( PostProcessor.class );
        this.controller.postProcessor( this.postProcessor );
        this.controller.context( this.context );

        this.scriptDir = ResourceKey.from( "mymodule-1.0.0:/service/test" );
        this.controller.scriptDir( this.scriptDir );

        this.request = new JsHttpRequest();
        this.context.setRequest( this.request );

        this.scriptLoader = Mockito.mock( ScriptLoader.class );
        Mockito.when( this.scriptRunner.getLoader() ).thenReturn( this.scriptLoader );
    }

    @Test
    public void testExecute()
    {
        this.request.setMethod( "GET" );

        final ScriptSource script = Mockito.mock( ScriptSource.class );
        final ResourceKey scriptKey = this.scriptDir.resolve( "get.js" );
        Mockito.when( this.scriptLoader.load( scriptKey ) ).thenReturn( script );

        this.controller.execute();
        assertEquals( JsHttpResponse.STATUS_OK, this.response.getStatus() );

        Mockito.verify( this.scriptRunner ).source( script );
        Mockito.verify( this.scriptRunner ).execute();
    }

    @Test
    public void testExecutePostProcess()
    {
        this.request.setMethod( "GET" );
        this.response.setPostProcess( true );

        final ScriptSource script = Mockito.mock( ScriptSource.class );
        final ResourceKey scriptKey = this.scriptDir.resolve( "get.js" );
        Mockito.when( this.scriptLoader.load( scriptKey ) ).thenReturn( script );

        this.controller.execute();
        assertEquals( JsHttpResponse.STATUS_OK, this.response.getStatus() );

        Mockito.verify( this.scriptRunner ).source( script );
        Mockito.verify( this.scriptRunner ).execute();
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
