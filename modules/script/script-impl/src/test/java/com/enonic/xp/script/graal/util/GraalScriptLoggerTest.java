package com.enonic.xp.script.graal.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.graal.GraalJSContextFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class GraalScriptLoggerTest
{
    private Context context;

    private Logger log;

    @BeforeEach
    void setUp()
    {
        this.context = new GraalJSContextFactory().create();
        this.log = Mockito.mock( Logger.class );

        final GraalScriptLogger logger =
            new GraalScriptLogger( this.context, ResourceKey.from( "myapp:/test.js" ),
                                   new GraalJavascriptHelperFactory().create( this.context ).objectConverter(), this.log );
        this.context.getBindings( "js" ).putMember( "log", logger.asProxyObject() );
    }

    @AfterEach
    void destroy()
    {
        this.context.close();
    }

    @Test
    void formatsSingleArgument()
    {
        eval( "log.debug('debug %s', 'x')" );
        eval( "log.info('info %s', 42)" );
        eval( "log.warning('warning %s', true)" );
        eval( "log.error('error %s', {a: 1})" );

        verify( log ).debug( "(/test.js) debug x" );
        verify( log ).info( "(/test.js) info 42" );
        verify( log ).warn( "(/test.js) warning true" );
        verify( log ).error( "(/test.js) error {\"a\":1}" );
    }

    @Test
    void formatsSeveralArguments()
    {
        eval( "log.info('%s %s', 'x', [1, 2])" );

        verify( log ).info( "(/test.js) x [1,2]" );
    }

    @Test
    void logsMessageWithoutArgumentsAsIs()
    {
        eval( "log.info('100%')" );

        verify( log ).info( "(/test.js) 100%" );
    }

    @Test
    void format()
    {
        assertEquals( "(/test.js) one x", eval( "log.format('one %s', 'x')" ) );
        assertEquals( "(/test.js) none %s", eval( "log.format('none %s')" ) );
    }

    @Test
    void logsUnformattableMessageWithArguments()
    {
        eval( "log.info('50% done %s', 'x')" );
        eval( "log.info('%s and %s', 'x')" );

        verify( log ).info( "(/test.js) 50% done %s [x]" );
        verify( log ).info( "(/test.js) %s and %s [x]" );
    }

    @Test
    void prefixesMessageWithException()
    {
        eval( "log.debug('failed', new Error('boom'))" );
        eval( "log.info('failed', new Error('boom'))" );
        eval( "log.warning('failed', new Error('boom'))" );
        eval( "try { null.x } catch (e) { log.error('failed', e) }" );

        verify( log ).debug( eq( "(/test.js) failed" ), any( PolyglotException.class ) );
        verify( log ).info( eq( "(/test.js) failed" ), any( PolyglotException.class ) );
        verify( log ).warn( eq( "(/test.js) failed" ), any( PolyglotException.class ) );
        verify( log ).error( eq( "(/test.js) failed" ), any( PolyglotException.class ) );
    }

    @Test
    void logsExceptionPassedAlone()
    {
        eval( "log.error(new Error('boom'))" );

        verify( log ).error( eq( "(/test.js) Error: boom" ), any( PolyglotException.class ) );
    }

    @Test
    void logsTrailingException()
    {
        eval( "log.error('failed %s %s', 'a', 1, new Error('boom'))" );
        eval( "try { java.lang.Integer.parseInt('x') } catch (e) { log.warning('failed %s', 'b', e) }" );

        verify( log ).error( eq( "(/test.js) failed a 1" ), any( PolyglotException.class ) );
        verify( log ).warn( eq( "(/test.js) failed b" ), any( PolyglotException.class ) );
    }

    @Test
    void logsErrorCause()
    {
        final Throwable logged = logError( "new Error('outer', {cause: new Error('middle', {cause: new TypeError('inner')})})" );

        assertEquals( "Error: middle", logged.getCause().getMessage() );
        assertEquals( "TypeError: inner", logged.getCause().getCause().getMessage() );
        assertNull( logged.getCause().getCause().getCause() );
    }

    @Test
    void logsJavaExceptionCause()
    {
        final Throwable logged =
            logError( "new Error('outer', {cause: (() => { try { java.lang.Integer.parseInt('x') } catch (e) { return e } })()})" );

        assertEquals( "For input string: \"x\"", logged.getCause().getMessage() );
    }

    @Test
    void limitsCauseDepth()
    {
        Throwable logged = logError( "var e = new Error('0'); for (var i = 1; i < 100; i++) { e = new Error(String(i), {cause: e}) } e" );
        int depth = 0;
        while ( logged.getCause() != null )
        {
            logged = logged.getCause();
            depth++;
        }

        assertEquals( 16, depth );
    }

    @Test
    void ignoresCauseThatIsNotError()
    {
        assertNull( logError( "new Error('outer', {cause: 'text'})" ).getCause() );
    }

    @Test
    void ignoresCauseThatThrows()
    {
        assertNull( logError( "Object.defineProperty(new Error('outer'), 'cause', {get() { throw new Error('getter') }})" ).getCause() );
    }

    @Test
    void stopsAtCyclicCause()
    {
        final Throwable logged = logError( "var a = new Error('a'); a.cause = new Error('b', {cause: a}); a" );

        assertEquals( "Error: b", logged.getCause().getMessage() );
        assertNull( logged.getCause().getCause() );
    }

    @Test
    void stopsAtSelfCause()
    {
        assertNull( logError( "var a = new Error('a'); a.cause = a; a" ).getCause() );
    }

    private Throwable logError( final String error )
    {
        context.getBindings( "js" ).putMember( "error", context.eval( "js", error ) );
        eval( "log.error('failed', error)" );
        final ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass( Throwable.class );
        verify( log ).error( eq( "(/test.js) failed" ), captor.capture() );
        return captor.getValue();
    }

    private String eval( final String script )
    {
        return this.context.eval( "js", script ).toString();
    }
}
