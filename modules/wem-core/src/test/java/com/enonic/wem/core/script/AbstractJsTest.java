package com.enonic.wem.core.script;

import org.junit.Before;

import junit.framework.Assert;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.script.service.ScriptServiceImpl;

public abstract class AbstractJsTest
{
    private ScriptServiceImpl scriptService;

    @Before
    public final void setup()
        throws Exception
    {
        /*
        this.scriptService = new ScriptServiceImpl();
        this.scriptService.setCompiler( new ScriptCompilerImpl( new ScriptCacheImpl() ) );

        final File modulesDir = new File( getClass().getResource( "/modules" ).getFile() );
        final SystemConfig systemConfig = Mockito.mock( SystemConfig.class );
        Mockito.when( systemConfig.getModulesDir() ).thenReturn( modulesDir.toPath() );

        final ClassLoader systemClassLoader = new URLClassLoader( new URL[]{new File( modulesDir, "system" ).toURI().toURL()} );

        final ResourceServiceImpl resourceService = new ResourceServiceImpl( systemConfig, systemClassLoader );
        this.scriptService.setResourceService( resourceService );
        */
    }

    protected final void execTest( final String path )
    {
        final ScriptRunner runner = this.scriptService.newRunner();
        runner.source( ModuleResourceKey.from( path ) );
        runner.binding( "test", new TestUtils() );
        runner.execute();
    }

    public final class TestUtils
    {
        public void assertTrue( final boolean value, final String message )
        {
            Assert.assertTrue( message, value );
        }

        public void assertEquals( final String expected, final String value )
        {
            Assert.assertEquals( expected, value );
        }

        public void println( final String message )
        {
            System.out.println( message );
        }
    }
}
