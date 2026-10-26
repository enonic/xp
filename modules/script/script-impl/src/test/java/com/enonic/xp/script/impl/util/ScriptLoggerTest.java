package com.enonic.xp.script.impl.util;

import javax.script.ScriptEngine;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.impl.AbstractScriptTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class ScriptLoggerTest
    extends AbstractScriptTest
{
    @Test
    void testLog()
    {
        runTestScript( "logging/log-test.js" );
    }

    @Test
    void testFormat()
    {
        runTestScript( "logging/format-test.js" );
    }

    @Test
    void prefixesMessageWithException()
        throws Exception
    {
        final Logger log = Mockito.mock( Logger.class );
        final ScriptEngine engine = NashornHelper.getScriptEngine( getClass().getClassLoader() );
        final JsObjectConverter converter = new JsObjectConverter( new JavascriptHelperFactory( engine ).create() );
        engine.put( "log", new ScriptLogger( ResourceKey.from( "myapp:/test.js" ), converter, log ) );

        engine.eval( "log.debug('failed', new java.lang.IllegalStateException('boom'))" );
        engine.eval( "try { null.x } catch (e) { log.info('failed', e) }" );
        engine.eval( "log.warning('failed', new Error('boom'))" );
        engine.eval( "log.error('100%', new Error('boom'))" );

        verify( log ).debug( eq( "(/test.js) failed" ), any( IllegalStateException.class ) );
        verify( log ).info( eq( "(/test.js) failed" ), any( Exception.class ) );
        verify( log ).warn( eq( "(/test.js) failed" ), any( Exception.class ) );
        verify( log ).error( eq( "(/test.js) 100%" ), any( Exception.class ) );
    }
}
