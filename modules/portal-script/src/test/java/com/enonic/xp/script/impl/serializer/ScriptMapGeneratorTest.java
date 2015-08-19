package com.enonic.xp.script.impl.serializer;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.script.impl.AbstractScriptTest;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.serializer.MapSerializable;

public class ScriptMapGeneratorTest
    extends AbstractScriptTest
{
    @Test
    public void testObject()
    {
        final Object obj = (MapSerializable) gen -> {
            gen.value( "a", 1 );
            gen.value( "b", 2 );
        };

        final ScriptExports exports = runTestScript( "serializer/serializer-test.js" );
        exports.executeMethod( "testObject", obj );
    }

    @Test
    public void testArray()
    {
        final Object obj = (MapSerializable) gen -> {
            gen.value( "a", 1 );
            gen.value( "b", 2 );
        };

        final List<Object> list = Lists.newArrayList( obj, 2 );
        final ScriptExports exports = runTestScript( "serializer/serializer-test.js" );
        exports.executeMethod( "testArray", list );
    }
}
