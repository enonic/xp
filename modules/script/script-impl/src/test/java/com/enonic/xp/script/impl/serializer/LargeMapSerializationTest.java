package com.enonic.xp.script.impl.serializer;

import org.junit.jupiter.api.Test;

import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.AbstractScriptTest;
import com.enonic.xp.script.serializer.MapSerializable;

/**
 * Objects with more than 512 properties used to lose properties, or pick up properties belonging to
 * another object, once the same map had been serialized more than once - the shape produced by
 * {@code i18n.getPhrases()} for a large bundle. See enonic/xp#6882.
 */
class LargeMapSerializationTest
    extends AbstractScriptTest
{
    private static final int PHRASE_COUNT = 1200;

    @Test
    void largeMapKeepsEveryProperty()
    {
        final MapSerializable phrases = gen -> {
            for ( int i = 0; i < PHRASE_COUNT; i++ )
            {
                gen.value( "phrase.key.number." + i, "value-" + i );
            }
        };

        final ScriptExports exports = runTestScript( "serializer/large-map-test.js" );

        // Every call builds a new JS object with the same keys in the same order, so the objects share the
        // engine's internal property map. Reading each one back before the next is built is what used to
        // corrupt the shared map.
        for ( int round = 0; round < 5; round++ )
        {
            exports.executeMethod( "checkPhrases", phrases, PHRASE_COUNT );
        }
    }
}
