package com.enonic.wem.portal.controller;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.portal.postprocess.PostProcessor;
import com.enonic.wem.script.ScriptRunner;

import static org.junit.Assert.*;

public class JsControllerImplTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ScriptRunner scriptRunner;

    private JsControllerImpl controller;

    private PostProcessor postProcessor;

    private JsContext context;

    private JsHttpRequest request;

    private JsHttpResponse response;

    private void writeFile( final File dir, final String path, final String value )
        throws Exception
    {
        final File file = new File( dir, path );
        file.getParentFile().mkdirs();
        ByteSource.wrap( value.getBytes( Charsets.UTF_8 ) ).copyTo( new FileOutputStream( file ) );
    }

    @Before
    public void setup()
        throws Exception
    {
        final File modulesDir = this.temporaryFolder.newFolder( "modules" );

        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.register( ModuleKey.from( "mymodule-1.0.0" ), new File( modulesDir, "mymodule-1.0.0" ) );

        writeFile( modulesDir, "mymodule-1.0.0/service/test/get.js", "1+1" );

        this.context = new JsContext();
        this.response = this.context.getResponse();

        this.scriptRunner = Mockito.mock( ScriptRunner.class );
        this.controller = new JsControllerImpl( this.scriptRunner );

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
        Mockito.verify( this.scriptRunner ).execute();
    }

    @Test
    public void testExecutePostProcess()
    {
        this.request.setMethod( "GET" );
        this.response.setPostProcess( true );
        this.controller.execute();

        assertEquals( JsHttpResponse.STATUS_OK, this.response.getStatus() );
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
