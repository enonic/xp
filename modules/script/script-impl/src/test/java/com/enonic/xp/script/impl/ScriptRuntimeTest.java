package com.enonic.xp.script.impl;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.runtime.BootstrapParams;
import com.enonic.xp.script.serializer.MapSerializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ScriptRuntimeTest
    extends AbstractScriptTest
{
    @Test
    void testEmpty()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/empty-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertFalse( exports.hasMethod( "hello" ) );
    }

    @Test
    void testExecuteExported()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/export-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
        assertTrue( exports.hasMethod( "hello" ) );
        assertEquals( "Hello World!", exports.executeMethod( "hello", "World" ).getValue() );
        // a bound scope yields a view onto the same exports — on a pooled engine one pinned to the
        // executing context, so the view's identity is not part of the contract, its behavior is
        final ScriptExports bound = exports.executeBound( view -> view );
        assertEquals( "Hello World!", bound.executeMethod( "hello", "World" ).getValue() );
        exports.retain();
        exports.release();
    }

    @Test
    void testExecuteExported_objectArg()
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
    void testResolve()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/resolve/resolve-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
    }

    @Test
    void testRequire()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/site/require/require-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
    }

    @Test
    void testRequire_3rd()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/site/require/3rd/require-3rd-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
    }

    @Test
    void testCompileError()
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
    void testRuntimeError()
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
    void testCache()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/empty-test.js" );

        // read up front: an invalidated application's exports belong to a torn-down executor,
        // which engines that own script contexts close
        final Object cached = runTestScript( script ).getRawValue();
        assertSame( cached, runTestScript( script ).getRawValue() );

        this.scriptRuntime.invalidate( ApplicationKey.from( "othermodule" ) );

        // another application's teardown leaves this one's module cache intact
        assertSame( cached, runTestScript( script ).getRawValue() );

        this.scriptRuntime.invalidate( script.getApplicationKey() );

        this.scriptRuntime.bootstrap( BootstrapParams.create().application( script.getApplicationKey() ).build() );

        // its own teardown drops it
        assertNotSame( cached, runTestScript( script ).getRawValue() );
    }
}
