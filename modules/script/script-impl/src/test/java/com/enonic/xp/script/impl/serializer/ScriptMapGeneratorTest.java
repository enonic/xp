package com.enonic.xp.script.impl.serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;

import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.AbstractScriptTest;
import com.enonic.xp.script.serializer.MapSerializable;

public class ScriptMapGeneratorTest
    extends AbstractScriptTest
{
    @Test
    public void testObject()
    {
        final Object obj = (MapSerializable) gen -> {
            gen.value( "a", 1 );
            gen.value( "b", "2" );
            gen.value( "c", 3L );
            gen.value( "d", 4.0f );
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

    @Test
    public void testMapValue()
    {
        final Map<String, Object> submap = new HashMap<>();
        submap.put( "child1", 1 );
        submap.put( "child2", 2 );

        final Map<String, Object> map = new HashMap<>();
        map.put( "value1", 1 );
        map.put( "value2", true );
        map.put( "value3", "string" );
        map.put( "value4", submap );

        final Object obj = (MapSerializable) gen -> {
            gen.value( "map", map );
            gen.value( "b", 2 );
        };

        final ScriptExports exports = runTestScript( "serializer/serializer-test.js" );
        exports.executeMethod( "testMapValue", obj );
    }

    @Test
    public void testListValue()
    {
        final List<Object> level2List = new ArrayList<>();
        level2List.add( "list2A" );
        level2List.add( "list2B" );
        final List<Object> level1List = new ArrayList<>();
        level1List.add( "list1A" );
        level1List.add( "list1B" );
        level1List.add( level2List );

        final Map<String, Object> map = new HashMap<>();
        map.put( "mapValue", 1 );
        final HashMultimap<Object, Object> multimap = HashMultimap.create();
        multimap.put( "multimapValue", 1 );
        multimap.put( "multimapValue", 2 );

        final List<Object> list = new ArrayList<>();
        list.add( 1 );
        list.add( true );
        list.add( "string" );
        list.add( level1List );
        list.add( map );
        list.add( multimap );

        final MapSerializable value = gen -> gen.value( "root", list );

        final ScriptExports exports = runTestScript( "serializer/serializer-test.js" );
        exports.executeMethod( "testListValue", value );
    }

    @Test
    public void testMultimapValue()
    {
        final HashMultimap<Object, Object> submap = HashMultimap.create();
        submap.put( "children", 5 );
        submap.put( "children", 6 );
        submap.put( "children", 7 );

        final HashMultimap<Object, Object> map = HashMultimap.create();
        map.put( "value1", 1 );
        map.put( "value1", 2 );
        map.put( "value1", 3 );
        map.put( "value2", true );
        map.put( "value2", false );
        map.put( "value3", "string1" );
        map.put( "value4", submap );

        final Object obj = (MapSerializable) gen -> gen.value( "multimap", map );

        final ScriptExports exports = runTestScript( "serializer/serializer-test.js" );
        exports.executeMethod( "testMultimapValue", obj );
    }
}
