package com.enonic.xp.script.graal;

import java.util.concurrent.atomic.AtomicInteger;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
