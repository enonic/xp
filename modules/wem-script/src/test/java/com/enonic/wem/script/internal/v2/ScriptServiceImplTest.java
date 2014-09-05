package com.enonic.wem.script.internal.v2;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.resource.ResourceKey;
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
        final TestScriptLibraries libraries = new TestScriptLibraries();
        this.service = new ScriptServiceImpl( libraries );
    }

    @Test
    public void testEmpty()
    {
        final ResourceKey script = ResourceKey.from( "mymodule-1.0.0:/empty-test.js" );

        final ScriptExports exports = this.service.execute( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertFalse( exports.hasProperty( "hello" ) );
    }

    @Test
    public void testExecuteExported()
    {
        final ResourceKey script = ResourceKey.from( "mymodule-1.0.0:/export-test.js" );

        final ScriptExports exports = this.service.execute( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertTrue( exports.hasProperty( "hello" ) );
        assertEquals( "Hello World!", exports.executeMethod( "hello", "World" ) );
    }

    @Test
    public void testResolve()
    {
        final ResourceKey script = ResourceKey.from( "mymodule-1.0.0:/resolve/resolve-test.js" );

        final ScriptExports exports = this.service.execute( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertEquals( ResourceKey.from( "mymodule-1.0.0:/resolve/other.js" ), exports.executeMethod( "test", "other.js" ) );
        assertEquals( ResourceKey.from( "mymodule-1.0.0:/other/other.js" ), exports.executeMethod( "test", "../other/other.js" ) );
    }

    @Test
    public void testRequire()
    {
        final ResourceKey script = ResourceKey.from( "mymodule-1.0.0:/require/require-test.js" );

        final ScriptExports exports = this.service.execute( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertEquals( "Hello World!", exports.executeMethod( "test", "World" ) );
    }

    @Test
    public void testResolveRequire()
    {
        final ResourceKey script = ResourceKey.from( "mymodule-1.0.0:/resolve/resolve-require-test.js" );

        final ScriptExports exports = this.service.execute( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertEquals( ResourceKey.from( "other-1.0.0:/util.js" ), exports.executeMethod( "test", "util.js" ) );
    }
}
