package com.enonic.wem.script.internal;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.resource.ResourceUrlRegistry;
import com.enonic.wem.api.resource.ResourceUrlTestHelper;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.script.ScriptService;

import static org.junit.Assert.*;

public class ScriptServiceImplTest
{
    private ScriptService service;

    public ScriptServiceImplTest()
    {
        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );
    }

    @Before
    public void setup()
    {
        final ScriptEnvironment environment = new ScriptEnvironment();
        this.service = new ScriptServiceImpl( environment );
    }

    @Test
    public void testEmpty()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/empty-test.js" );

        final ScriptExports exports = this.service.execute( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertFalse( exports.hasProperty( "hello" ) );
    }

    @Test
    public void testExecuteExported()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/export-test.js" );

        final ScriptExports exports = this.service.execute( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertTrue( exports.hasProperty( "hello" ) );
        assertEquals( "Hello World!", exports.executeMethod( "hello", "World" ) );
    }

    @Test
    public void testResolve()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/resolve/resolve-test.js" );
        final ScriptExports exports = this.service.execute( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
    }

    @Test
    public void testRequire()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/require/require-test.js" );
        final ScriptExports exports = this.service.execute( script );
        assertNotNull( exports );
    }

    @Test
    public void testCompileError()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/error/error-test.js" );

        try
        {
            this.service.execute( script );
            fail( "Should throw ResourceProblemException" );
        }
        catch ( final ResourceProblemException e )
        {
            assertEquals( 1, e.getLineNumber() );
            assertEquals( script, e.getResource() );
        }
    }

    @Test
    public void testRuntimeError()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/error/error-in-export-test.js" );
        final ScriptExports exports = this.service.execute( script );

        assertNotNull( exports );

        try
        {
            exports.executeMethod( "hello" );
            fail( "Should throw ResourceProblemException" );
        }
        catch ( final ResourceProblemException e )
        {
            assertEquals( 1, e.getLineNumber() );
            assertEquals( ResourceKey.from( "mymodule:/error/error-test.js" ), e.getResource() );
        }
    }
}
