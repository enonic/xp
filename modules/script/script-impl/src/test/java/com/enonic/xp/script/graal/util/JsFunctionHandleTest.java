package com.enonic.xp.script.graal.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.impl.util.ObjectConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsFunctionHandleTest
{
    private Context context;

    private ExecutorService executor;

    private ScheduledExecutorService scheduler;

    @BeforeEach
    void setUp()
    {
        this.context = new GraalJSContextFactory().create();
        this.executor = Executors.newSingleThreadExecutor();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @AfterEach
    void destroy()
    {
        this.scheduler.shutdownNow();
        this.executor.shutdownNow();
        this.context.close();
    }

    @Test
    @Timeout(30)
    void functionParameterBecomesHandle()
        throws Exception
    {
        final CallbackHolder holder = new CallbackHolder();
        context.eval( "js", "(function (h) { h.setCallback(function (x) { return x * 2; }); })" ).execute( holder );

        assertInstanceOf( JsFunctionHandle.class, holder.callback );

        // invocable from a foreign thread
        final Object result = executor.submit( () -> holder.callback.apply( 5 ) ).get();
        assertEquals( 10, ( (Number) result ).intValue() );
    }

    @Test
    @Timeout(30)
    void consumerParameterBecomesHandle()
        throws Exception
    {
        final ListenerHolder holder = new ListenerHolder();
        context.eval( "js", "(function (h) { h.setListener(function (e) { h.log(e); }); })" ).execute( holder );

        assertInstanceOf( JsFunctionHandle.class, holder.listener );

        executor.submit( () -> holder.listener.accept( "event" ) ).get();
        assertEquals( "event", holder.received );
    }

    @Test
    @Timeout(30)
    void callbackSerializesWithBusyContext()
        throws Exception
    {
        final Blocker blocker = new Blocker();
        final Value busy = context.eval( "js", "(function (b) { return b.enter(); })" );
        final Function<Object, Object> callback =
            context.eval( "js", "(function (x) { return x + 1; })" ).as( Function.class );

        assertInstanceOf( JsFunctionHandle.class, callback );

        // occupy the context from another thread under the same discipline the executor uses
        final Future<Integer> busyResult = executor.submit( () -> {
            synchronized ( context )
            {
                return busy.execute( blocker ).asInt();
            }
        } );
        assertTrue( blocker.entered.await( 10, TimeUnit.SECONDS ) );

        // without routing, this invocation enters the busy context concurrently and throws
        // "Multi threaded access requested..."; with the handle it blocks until the context frees up
        scheduler.schedule( blocker.release::countDown, 300, TimeUnit.MILLISECONDS );
        final Object result = callback.apply( 41 );

        assertEquals( 42, ( (Number) result ).intValue() );
        assertEquals( 42, busyResult.get( 10, TimeUnit.SECONDS ).intValue() );
    }

    @Test
    @Timeout(30)
    void convertsResultsEagerly()
        throws Exception
    {
        final Function<Object, Object> fn = context.eval( "js",
                                                          "(function () { return { num: 1, arr: [1, 2], nested: function () { return 'x'; } }; })" )
            .as( Function.class );

        final Object result = executor.submit( () -> fn.apply( null ) ).get();

        final Map<?, ?> map = assertInstanceOf( Map.class, result );
        assertEquals( 1, ( (Number) map.get( "num" ) ).intValue() );
        assertEquals( List.of( 1, 2 ), ( (List<?>) map.get( "arr" ) ).stream().map( v -> ( (Number) v ).intValue() ).toList() );

        final JsFunctionHandle nested = assertInstanceOf( JsFunctionHandle.class, map.get( "nested" ) );
        assertEquals( "x", executor.submit( () -> nested.execute() ).get() );
    }

    @Test
    @Timeout(30)
    void converterFunctionsSpreadArguments()
        throws Exception
    {
        final ObjectConverter converter = new GraalJavascriptHelperFactory().create( context ).objectConverter();

        final Object fn = converter.fromJs( context.eval( "js", "(function (a, b) { return a + b; })" ) );

        @SuppressWarnings("unchecked") final Function<Object[], Object> function = assertInstanceOf( Function.class, fn );
        final Object result = executor.submit( () -> function.apply( new Object[]{20, 22} ) ).get();
        assertEquals( 42, ( (Number) result ).intValue() );
    }

    @Test
    @Timeout(30)
    void runnableSupplierAndPredicateBecomeHandles()
        throws Exception
    {
        final MultiHolder holder = new MultiHolder();
        context.eval( "js", "(function (h) {" + //
            " h.setRunnable(function () { h.log('ran'); });" + //
            " h.setSupplier(function () { return 7; });" + //
            " h.setPredicate(function (x) { return x > 3; });" + //
            "})" ).execute( holder );

        assertInstanceOf( JsFunctionHandle.class, holder.runnable );
        assertInstanceOf( JsFunctionHandle.class, holder.supplier );
        assertInstanceOf( JsFunctionHandle.class, holder.predicate );

        executor.submit( holder.runnable ).get();
        assertEquals( "ran", holder.received );
        assertEquals( 7, ( (Number) executor.submit( () -> holder.supplier.get() ).get() ).intValue() );
        assertTrue( executor.submit( () -> holder.predicate.test( 5 ) ).get() );
        assertFalse( executor.submit( () -> holder.predicate.test( 1 ) ).get() );
    }

    @Test
    @Timeout(30)
    void convertsScalarResults()
        throws Exception
    {
        @SuppressWarnings("unchecked") final Function<Object, Object> fn = context.eval( "js", "(function (kind) {" + //
            " if (kind === 'date') return new Date(1000);" + //
            " if (kind === 'bool') return true;" + //
            " if (kind === 'none') return null;" + //
            " return 's';" + //
            "})" ).as( Function.class );

        assertEquals( new java.util.Date( 1000 ), executor.submit( () -> fn.apply( "date" ) ).get() );
        assertEquals( Boolean.TRUE, executor.submit( () -> fn.apply( "bool" ) ).get() );
        assertNull( executor.submit( () -> fn.apply( "none" ) ).get() );
        assertEquals( "s", executor.submit( () -> fn.apply( "str" ) ).get() );
    }

    @Test
    @Timeout(30)
    void convertsNullEntriesLikeObjectConverter()
        throws Exception
    {
        @SuppressWarnings("unchecked") final Function<Object, Object> fn = context.eval( "js",
            "(function () { return { a: 1, b: null, list: [1, null, 2] }; })" ).as( Function.class );

        // the same JS value must convert identically whether it crosses via ObjectConverter.fromJs
        // or a handle's return value: null-valued keys stay in objects, null elements drop from arrays
        final Map<?, ?> result = (Map<?, ?>) executor.submit( () -> fn.apply( null ) ).get();
        assertTrue( result.containsKey( "b" ) );
        assertNull( result.get( "b" ) );
        assertEquals( 2, ( (List<?>) result.get( "list" ) ).size() );
    }

    @Test
    @Timeout(30)
    void returnsHostObjectsUnwrapped()
        throws Exception
    {
        @SuppressWarnings("unchecked") final Function<Object, Object> fn =
            context.eval( "js", "(function (o) { return o; })" ).as( Function.class );

        final Object host = new CallbackHolder();
        assertSame( host, executor.submit( () -> fn.apply( host ) ).get() );
    }

    @Test
    @Timeout(30)
    void translatesErrors()
    {
        @SuppressWarnings("unchecked") final Function<Object, Object> fn =
            context.eval( "js", "(function () { throw new Error('boom'); })" ).as( Function.class );

        assertThrows( RuntimeException.class, () -> fn.apply( null ) );
    }

    public static class CallbackHolder
    {
        volatile Function<Object, Object> callback;

        public void setCallback( final Function<Object, Object> callback )
        {
            this.callback = callback;
        }
    }

    public static class ListenerHolder
    {
        volatile Consumer<Object> listener;

        volatile Object received;

        public void setListener( final Consumer<Object> listener )
        {
            this.listener = listener;
        }

        public void log( final Object value )
        {
            this.received = value;
        }
    }

    public static class MultiHolder
    {
        volatile Runnable runnable;

        volatile Supplier<Object> supplier;

        volatile Predicate<Object> predicate;

        volatile Object received;

        public void setRunnable( final Runnable runnable )
        {
            this.runnable = runnable;
        }

        public void setSupplier( final Supplier<Object> supplier )
        {
            this.supplier = supplier;
        }

        public void setPredicate( final Predicate<Object> predicate )
        {
            this.predicate = predicate;
        }

        public void log( final Object value )
        {
            this.received = value;
        }
    }

    public static class Blocker
    {
        final CountDownLatch entered = new CountDownLatch( 1 );

        final CountDownLatch release = new CountDownLatch( 1 );

        public int enter()
            throws InterruptedException
        {
            entered.countDown();
            release.await();
            return 42;
        }
    }
}
