package com.enonic.wem.core.script;

import org.junit.Before;

import junit.framework.Assert;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.module.source.SourceResolverImpl;
import com.enonic.wem.core.script.cache.ScriptCacheImpl;
import com.enonic.wem.core.script.engine.ScriptEngineServiceImpl;
import com.enonic.wem.core.script.service.ScriptServiceImpl;

public abstract class AbstractJsTest
{
    private ScriptServiceImpl scriptService;

    @Before
    public final void setup()
    {
        this.scriptService = new ScriptServiceImpl();
        this.scriptService.setSourceResolver( new SourceResolverImpl( null ) );

        final ScriptEngineServiceImpl scriptEngineService = new ScriptEngineServiceImpl( new ScriptCacheImpl() );
        this.scriptService.setScriptEngineService( scriptEngineService );
    }

    protected final void execTest( final String path )
    {
        final ModuleResourceKey resource = new ModuleResourceKey( ModuleKey.SYSTEM, ResourcePath.from( path ) );
        final ScriptRunner runner = this.scriptService.newRunner( resource );
        runner.variable( "test", new TestUtils() );
        runner.execute();
    }

    public final class TestUtils
    {
        public void assertTrue( final boolean value, final String message )
        {
            Assert.assertTrue( message, value );
        }
    }
}
