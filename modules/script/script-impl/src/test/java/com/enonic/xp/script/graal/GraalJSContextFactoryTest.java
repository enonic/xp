package com.enonic.xp.script.graal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraalJSContextFactoryTest
{
    @Test
    void engineIsResolvedWithTheFirstContext()
    {
        final AtomicInteger resolved = new AtomicInteger();
        final Engine engine = Engine.newBuilder().build();
        try
        {
            final GraalJSContextFactory factory = new GraalJSContextFactory( getClass().getClassLoader(), () -> {
                resolved.incrementAndGet();
                return engine;
            } );

            // an application that never executes a script gets an executor, and with it this
            // factory — but no engine
            assertEquals( 0, resolved.get() );

            try ( Context context = factory.create() )
            {
                assertEquals( 1, resolved.get() );
                assertEquals( 42, context.eval( "js", "42" ).asInt() );
            }
        }
        finally
        {
            engine.close();
        }
    }

    @Test
    void poolGrowthBuildsMoreThanOneContextOnTheSharedEngine()
    {
        final Engine engine = Engine.newBuilder().build();
        try
        {
            final GraalJSContextFactory factory = new GraalJSContextFactory( getClass().getClassLoader(), () -> engine );

            try ( Context first = factory.create(); Context second = factory.create() )
            {
                assertEquals( 1, first.eval( "js", "1" ).asInt() );
                assertEquals( 2, second.eval( "js", "2" ).asInt() );
            }
        }
        finally
        {
            engine.close();
        }
    }

    @Test
    void twoApplicationsBuildContextsOnTheSharedEngine()
    {
        final Engine engine = Engine.newBuilder().build();
        try
        {
            final GraalJSContextFactory one = new GraalJSContextFactory( getClass().getClassLoader(), () -> engine );
            final GraalJSContextFactory two = new GraalJSContextFactory( getClass().getClassLoader(), () -> engine );

            try ( Context first = one.create(); Context second = two.create() )
            {
                assertEquals( 1, first.eval( "js", "1" ).asInt() );
                assertEquals( 2, second.eval( "js", "2" ).asInt() );
            }
        }
        finally
        {
            engine.close();
        }
    }

    // a handle that locked anything but the context its creator built — the restricted wrapper
    // Context.getCurrent() returns, say — would leave two monitors guarding one context, and the
    // mutual exclusion every other execution path relies on would be a no-op
    @Test
    void handleLocksTheContextItsCreatorBuilt()
    {
        final Engine engine = Engine.newBuilder().build();
        try
        {
            final GraalJSContextFactory factory = new GraalJSContextFactory( getClass().getClassLoader(), () -> engine );

            try ( Context context = factory.create() )
            {
                final LockProbe probe = new LockProbe( context );
                context.getBindings( "js" ).putMember( "probe", probe );
                context.eval( "js", "(function () { probe.record(); })" ).as( Runnable.class ).run();

                assertTrue( probe.heldCreatorMonitor );
            }
        }
        finally
        {
            engine.close();
        }
    }

    // scripts read the polyglot bindings through Java.type('org.graalvm.polyglot.Context'), so a
    // bare context there would be close() one property read away. Pinned: the binding offers
    // nothing to call and nothing to enumerate, so none is reached by accident. Not a claim that it
    // is unreachable — reflection still unwraps the holder, and scripts hold the host's authority
    // regardless
    @Test
    void polyglotBindingsExposeNothingCallableOnTheContext()
    {
        try ( Context context = new GraalJSContextFactory().create() )
        {
            final String reached = context.eval( "js", """
                (function () {
                  const bindings = Java.type( 'org.graalvm.polyglot.Context' ).getCurrent().getPolyglotBindings();
                  const held = bindings['com.enonic.xp.script.graal.creatorContext'];
                  try
                  {
                    held.close();
                    return 'closed the context';
                  }
                  catch ( e )
                  {
                    return Object.keys( held ).join( ',' );
                  }
                })()
                """ ).asString();

            assertEquals( "", reached );
        }
    }

    // a handle must lock the context that owns its function, not whichever execution is in
    // progress. Resolving from anything the executor publishes gets this wrong the moment one
    // application's handle is invoked inside another's execution
    @Test
    void handleLocksTheOwningContextWhenAnotherIsExecuting()
    {
        final Engine engine = Engine.newBuilder().build();
        try
        {
            final GraalJSContextFactory factory = new GraalJSContextFactory( getClass().getClassLoader(), () -> engine );

            try ( Context owner = factory.create(); Context other = factory.create() )
            {
                final LockProbe probe = new LockProbe( owner );
                owner.getBindings( "js" ).putMember( "probe", probe );

                other.eval( "js", "1" );
                owner.eval( "js", "(function () { probe.record(); })" ).as( Runnable.class ).run();

                assertTrue( probe.heldCreatorMonitor );
            }
        }
        finally
        {
            engine.close();
        }
    }


    // The conversion, not the invocation, is what picks the lock. This is the case the fix rests
    // on: a value owned by one context converted while a *different* context is entered — a host
    // object called from `other` converting a function belonging to `owner`. Resolving from
    // anything but the value's own context hands the handle the wrong monitor here, and the
    // mutual exclusion the executor relies on silently stops holding.
    @Test
    @Timeout(60)
    void conversionInsideAnotherContextsExecutionStillLocksTheOwner()
    {
        final Engine engine = Engine.newBuilder().build();
        try
        {
            final GraalJSContextFactory factory = new GraalJSContextFactory( getClass().getClassLoader(), () -> engine );

            try ( Context owner = factory.create(); Context other = factory.create() )
            {
                final LockProbe probe = new LockProbe( owner );
                owner.getBindings( "js" ).putMember( "probe", probe );

                final Converter converter = new Converter( owner.eval( "js", "(function () { probe.record(); })" ) );
                other.getBindings( "js" ).putMember( "converter", converter );

                // the conversion happens here, with `other` entered and `owner` not
                other.eval( "js", "converter.convert()" );

                converter.converted.run();
                assertTrue( probe.heldCreatorMonitor, "handle locked a context other than the one owning its function" );
            }
        }
        finally
        {
            engine.close();
        }
    }

    // the reported trigger was an application growing its pool under concurrent requests, so the
    // contexts arrive on the shared engine at once rather than one after another
    @Test
    @Timeout(60)
    void contextsAreBuiltConcurrentlyOnTheSharedEngine()
        throws Exception
    {
        final int count = 8;
        final Engine engine = Engine.newBuilder().build();
        final ExecutorService threads = Executors.newFixedThreadPool( count );
        try
        {
            final GraalJSContextFactory factory = new GraalJSContextFactory( getClass().getClassLoader(), () -> engine );
            final CyclicBarrier start = new CyclicBarrier( count );

            final List<Future<Context>> built = new ArrayList<>();
            for ( int i = 0; i < count; i++ )
            {
                built.add( threads.submit( () -> {
                    start.await( 30, TimeUnit.SECONDS );
                    return factory.create();
                } ) );
            }

            final List<Context> contexts = new ArrayList<>();
            for ( final Future<Context> future : built )
            {
                contexts.add( future.get( 30, TimeUnit.SECONDS ) );
            }
            try
            {
                for ( final Context context : contexts )
                {
                    assertEquals( 42, context.eval( "js", "42" ).asInt() );
                }
            }
            finally
            {
                contexts.forEach( Context::close );
            }
        }
        finally
        {
            threads.shutdownNow();
            engine.close();
        }
    }

    // the trade the fix accepts: the creator binding is script-reachable, so a script can break
    // conversion for its own context. Pinned so the blast radius stays visible and stays local —
    // it must fail loudly here, and not at all for the context next door
    @Test
    @Timeout(60)
    void aScriptThatReplacesTheCreatorBindingBreaksOnlyItsOwnContext()
    {
        final Engine engine = Engine.newBuilder().build();
        try
        {
            final GraalJSContextFactory factory = new GraalJSContextFactory( getClass().getClassLoader(), () -> engine );

            try ( Context broken = factory.create(); Context healthy = factory.create() )
            {
                // member assignment is the way in — removeMember is not supported on these bindings
                broken.eval( "js", """
                    Java.type( 'org.graalvm.polyglot.Context' ).getCurrent().getPolyglotBindings()
                        ['com.enonic.xp.script.graal.creatorContext'] = 'hijacked';
                    """ );

                final RuntimeException e = assertThrows( RuntimeException.class,
                                                         () -> broken.eval( "js", "(function () {})" ).as( Runnable.class ).run() );
                assertTrue( e.getMessage().contains( "creator binding was replaced" ), e.getMessage() );

                // the neighbour is untouched
                final LockProbe probe = new LockProbe( healthy );
                healthy.getBindings( "js" ).putMember( "probe", probe );
                healthy.eval( "js", "(function () { probe.record(); })" ).as( Runnable.class ).run();
                assertTrue( probe.heldCreatorMonitor );
            }
        }
        finally
        {
            engine.close();
        }
    }

    public static final class Converter
    {
        private final Value function;

        Runnable converted;

        Converter( final Value function )
        {
            this.function = function;
        }

        public void convert()
        {
            this.converted = this.function.as( Runnable.class );
        }
    }

    public static final class LockProbe
    {
        private final Context context;

        boolean heldCreatorMonitor;

        LockProbe( final Context context )
        {
            this.context = context;
        }

        public void record()
        {
            this.heldCreatorMonitor = Thread.holdsLock( context );
        }
    }
}
