package com.enonic.xp.script.graal;

import java.util.concurrent.atomic.AtomicInteger;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
