package com.enonic.xp.script.graal;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;

import com.enonic.xp.script.graal.util.GraalJavascriptHelperFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GraalValueSharingTest
{
    @Test
    void guestValueLeakBetweenContextsFailsFast()
    {
        try (Engine engine = Engine.newBuilder().build())
        {
            final GraalJSContextFactory factory = new GraalJSContextFactory( null, engine );
            try (Context first = factory.create(); Context second = factory.create())
            {
                final Value leaked = first.eval( "js", "({key: 'value'})" );

                // a guest object of one context passed into another must throw, not silently
                // create a cross-context re-entry dependency
                assertThrows( RuntimeException.class, () -> second.getBindings( "js" ).putMember( "leak", leaked ) );

                // the supported way across contexts: convert to plain Java first
                final Object converted = new GraalJavascriptHelperFactory().create( first ).objectConverter().fromJs( leaked );
                second.getBindings( "js" ).putMember( "ok", converted );
                assertNotNull( second.getBindings( "js" ).getMember( "ok" ) );
            }
        }
    }
}
