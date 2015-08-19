package com.enonic.xp.script.impl;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.AbstractScriptTest;
import com.enonic.xp.script.serializer.MapSerializable;

import static org.junit.Assert.*;

public class ScriptRuntimeTest
    extends AbstractScriptTest
{
    @Test
    public void testEmpty()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/empty-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertFalse( exports.hasMethod( "hello" ) );
    }

    @Test
    public void testExecuteExported()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/export-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertTrue( exports.hasMethod( "hello" ) );
        assertEquals( "Hello World!", exports.executeMethod( "hello", "World" ).getValue() );
    }

    @Test
    public void testExecuteExported_objectArg()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/export-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertTrue( exports.hasMethod( "helloObject" ) );
        assertEquals( "Hello World!",
                      exports.executeMethod( "helloObject", (MapSerializable) gen -> gen.value( "name", "World" ) ).getValue() );
    }

    @Test
    public void testResolve()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/resolve/resolve-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
    }

    @Test
    public void testRequire()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/site/require/require-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
    }

    @Test
    public void testRequire_3rd()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/site/require/3rd/require-3rd-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
    }

    @Test
    public void testCompileError()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/error/error-test.js" );

        try
        {
            runTestScript( script );
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
        final ResourceKey script = ResourceKey.from( "myapplication:/error/error-in-export-test.js" );
        final ScriptExports exports = runTestScript( script );

        assertNotNull( exports );

        try
        {
            exports.executeMethod( "hello" );
            fail( "Should throw ResourceProblemException" );
        }
        catch ( final ResourceProblemException e )
        {
            assertEquals( 1, e.getLineNumber() );
            assertEquals( ResourceKey.from( "myapplication:/error/error-test.js" ), e.getResource() );
        }
    }

    @Test
    public void testCache()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/empty-test.js" );

        final ScriptExports exports1 = runTestScript( script );
        final ScriptExports exports2 = runTestScript( script );
        assertNotNull( exports2 );
        assertSame( exports1.getRawValue(), exports2.getRawValue() );

        this.scriptRuntime.invalidate( ApplicationKey.from( "othermodule" ) );

        final ScriptExports exports3 = runTestScript( script );
        assertSame( exports1.getRawValue(), exports3.getRawValue() );

        this.scriptRuntime.invalidate( script.getApplicationKey() );

        final ScriptExports exports4 = runTestScript( script );
        assertNotSame( exports1.getRawValue(), exports4.getRawValue() );
    }
}
